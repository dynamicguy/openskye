package org.skye.metadata.impl.jpa;

import com.google.common.base.Optional;
import com.google.inject.Provider;
import org.skye.core.ArchiveContentBlock;
import org.skye.core.ObjectMetadata;
import org.skye.core.ObjectSet;
import org.skye.core.SkyeException;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.domain.InformationStoreDefinition;
import org.skye.domain.Task;
import org.skye.domain.dao.ArchiveStoreDefinitionDAO;
import org.skye.domain.dao.InformationStoreDefinitionDAO;
import org.skye.metadata.ObjectMetadataRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the {@link ObjectMetadataRepository} using the Java
 * Persistence API (JPA) to act on database systems.
 */
public class JPAObjectMetadataRepository implements ObjectMetadataRepository
{
    @Inject
    private Provider<EntityManager> emf;

    @Inject
    protected ArchiveStoreDefinitionDAO archiveStores;

    @Inject
    protected InformationStoreDefinitionDAO informationStores;

    /**
     * Gets the {@link EntityManager} from the Guice-based Provider.
     *
     * @return The injected {@link EntityManager}.
     */
    protected EntityManager getEntityManager()
    {
        return emf.get();
    }

    /**
     * Creates a new {@link ObjectSet}, which is empty and can be used to
     * reference a set of {@link ObjectMetadata} stored on disk.
     *
     * @param name The name to be used for the {@link ObjectSet}.
     *
     * @return The new {@link ObjectSet}.
     */
    @Override
    public ObjectSet createObjectSet(String name)
    {
        JPAObjectSet jpaObjectSet = new JPAObjectSet();

        jpaObjectSet.setName(name);

        this.getEntityManager().persist(jpaObjectSet);

        return jpaObjectSet.toObjectSet();
    }

    /**
     * Deletes an existing {@link ObjectSet}, if it exists, or throws a
     * {@link SkyeException} if it does not.
     *
     * @param objectSet the object set to remove
     */
    @Override
    public void deleteObjectSet(ObjectSet objectSet)
    {
        Optional<JPAObjectSet> jpaObjectSet = this.getJpaObjectSet(objectSet.getId());

        if(!jpaObjectSet.isPresent())
            throw new SkyeException("The ObjectSet does not exist, and cannot be deleted.");

        this.getEntityManager().remove(jpaObjectSet.get());

        return;
    }

    /**
     * Adds an {@link ObjectMetadata} instance to the {@link ObjectSet}.
     *
     * @param objectSet      the object set
     *
     * @param objectMetadata the object metadata to add
     */
    @Override
    public void addObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata)
    {
        Optional<JPAObjectSet> jpaObjectSet = this.getJpaObjectSet(objectSet.getId());
        JPAObjectMetadata jpaObjectMetadata = new JPAObjectMetadata(objectMetadata);

        if(!jpaObjectSet.isPresent())
            throw new SkyeException("The ObjectSet to which you are attempting to add does not exist.");

        if(this.isObjectInSet(objectSet, objectMetadata))
            throw new SkyeException("The ObjectSet already contains the given ObjectMetadata");

        jpaObjectSet.get().getObjectMetadataSet().add(jpaObjectMetadata);
        this.getEntityManager().merge(jpaObjectSet.get());

        return;
    }

    /**
     * Removes an {@link ObjectMetadata} instance from the {@link ObjectSet}.
     *
     * @param objectSet      the object set
     *
     * @param objectMetadata the object metadata to remove
     */
    @Override
    public void removeObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata)
    {
        Optional<JPAObjectSet> jpaObjectSet = this.getJpaObjectSet(objectSet.getId());
        JPAObjectMetadata jpaObjectMetadata = new JPAObjectMetadata(objectMetadata);

        if(!jpaObjectSet.isPresent())
            throw new SkyeException("The ObjectSet from which you are attempting to remove does not exist.");

        if(!this.isObjectInSet(objectSet, objectMetadata))
            throw new SkyeException("The ObjectSet does not contain the given ObjectMetadata.");

        jpaObjectSet.get().getObjectMetadataSet().remove(jpaObjectMetadata);
        this.getEntityManager().merge(jpaObjectSet.get());

        return;
    }

    /**
     * Checks to see if the {@link ObjectMetadata} is found in the given
     * {@link ObjectSet}.
     *
     * @param objectSet The {@link ObjectSet} against which the query will run.
     *
     * @param objectMetadata The {@link ObjectMetadata} for which the query is
     *                       run.
     *
     * @return True if the {@link ObjectMetadata} is found in the
     * {@link ObjectSet}, or false if it is not found.
     */
    @Override
    public boolean isObjectInSet(ObjectSet objectSet, ObjectMetadata objectMetadata)
    {
        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<JPAObjectSet> root = cq.from(JPAObjectSet.class);
        SetJoin<JPAObjectSet, JPAObjectMetadata> objectToMetadata = root.join(JPAObjectSet_.objectMetadataSet);

        cq.select(cb.count(root));
        cq.where(cb.equal(objectToMetadata.get(JPAObjectMetadata_.id), objectMetadata.getId()));

        if(this.getEntityManager().createQuery(cq).getSingleResult() > 0)
            return true;

        return false;
    }

    /**
     * Gets the {@link @ObjectMetadata} associated with the given id.
     *
     * @param id The identifier of the object
     *
     * @return An Optional wrapper which will include the requested
     * {@link ObjectMetadata}, if it is found.
     */
    @Override
    public Optional<ObjectMetadata> get(String id)
    {
        JPAObjectMetadata jpaObjectMetadata = this.getEntityManager().find(JPAObjectMetadata.class, id);

        if(jpaObjectMetadata == null)
            return Optional.absent();

        return Optional.of(jpaObjectMetadata.toObjectMetadata());
    }

    /**
     * Either adds the {@link ObjectMetadata} to the repository, or, if it
     * already exists (by it's id property), removes the old
     * {@link ObjectMetadata} and replaces it with the new one.  If an
     * Exception occurs, then the repository is rolled back to it's previous
     * state, and the exception should be rethrown.
     *
     * @param objectMetadata The {@link ObjectMetadata} to be added to the
     *                       repository.
     */
    @Override
    public void put(ObjectMetadata objectMetadata)
    {
        JPAObjectMetadata jpaObjectMetadata = new JPAObjectMetadata(objectMetadata);

        if(this.get(jpaObjectMetadata.getId()).isPresent())
           this.getEntityManager().merge(jpaObjectMetadata);
        else
            this.getEntityManager().persist(jpaObjectMetadata);

        return;
    }

    /**
     * Based on an {@link InformationStoreDefinition}, return a collection of
     * {@link ObjectMetadata} which use the described
     * {@link org.skye.core.InformationStore}.
     *
     * @param informationStoreDefinition The {@link InformationStoreDefinition}
     *                                   which describes the information store.
     *
     * @return An {@link Iterable} collection of {@link ObjectMetadata} based
     * on the given {@link InformationStoreDefinition}.
     */
    @Override
    public Iterable<ObjectMetadata> getObjects(InformationStoreDefinition informationStoreDefinition)
    {
        List<ObjectMetadata> listObjectMetadata = new ArrayList<>();
        List<JPAObjectMetadata> listJpaObjectMetadata = null;
        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<JPAObjectMetadata> cq = cb.createQuery(JPAObjectMetadata.class);
        Root<JPAObjectMetadata> root = cq.from(JPAObjectMetadata.class);

        cq.select(root);
        cq.where(cb.equal(root.get(JPAObjectMetadata_.informationStoreId), informationStoreDefinition.getId()));

        listJpaObjectMetadata = this.getEntityManager().createQuery(cq).getResultList();

        for(JPAObjectMetadata jpa : listJpaObjectMetadata)
            listObjectMetadata.add(jpa.toObjectMetadata());

        return listObjectMetadata;
    }

    /**
     * Gets a collection of all of the {@link ObjectMetadata} related to a
     * given {@link Task}.
     *
     * @param task The {@link Task} for which the query will run.
     *
     * @return An {@link Iterable} collection containing all of the
     * {@link ObjectMetadata} for the given {@link Task}.
     */
    @Override
    public Iterable<ObjectMetadata> getObjects(Task task)
    {
        List<ObjectMetadata> listObjectMetadata = new ArrayList<>();
        List<JPAObjectMetadata> listJpaObjectMetadata = null;
        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<JPAObjectMetadata> cq = cb.createQuery(JPAObjectMetadata.class);
        Root<JPAObjectMetadata> root = cq.from(JPAObjectMetadata.class);

        cq.select(root);
        cq.where(cb.equal(root.get(JPAObjectMetadata_.taskId), task.getId()));

        listJpaObjectMetadata = this.getEntityManager().createQuery(cq).getResultList();

        for(JPAObjectMetadata jpa : listJpaObjectMetadata)
            listObjectMetadata.add(jpa.toObjectMetadata());

        return listObjectMetadata;

    }

    /**
     * Gets a collection containing all {@link ObjectMetadata} found in the
     * {@link ObjectSet}.
     *
     * @param objectSet The {@link ObjectSet} for which the query will be run.
     *
     * @return An {@link Iterable} collection containing all of the
     * {@link ObjectMetadata} in the given {@link ObjectSet}.
     */
    @Override
    public Iterable<ObjectMetadata> getObjects(ObjectSet objectSet)
    {
        List<ObjectMetadata> listObjectMetadata = new ArrayList<>();
        JPAObjectSet jpaObjectSet = null;
        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<JPAObjectSet> cq = cb.createQuery(JPAObjectSet.class);
        Root<JPAObjectSet> root = cq.from(JPAObjectSet.class);

        cq.select(root);
        cq.where(cb.equal(root.get(JPAObjectSet_.id), objectSet.getId()));

        jpaObjectSet = this.getEntityManager().createQuery(cq).getSingleResult();

        for(JPAObjectMetadata jpaObjectMetadata : jpaObjectSet.getObjectMetadataSet())
            listObjectMetadata.add(jpaObjectMetadata.toObjectMetadata());

        return listObjectMetadata;
    }

    /**
     * Gets the {@link ObjectSet} which has the given id.
     *
     * @param objectSetId the id of the object set for lookup
     *
     * @return An {@link Optional} wrapper containing the {@link ObjectSet},
     * if it is found.
     */
    @Override
    public Optional<ObjectSet> getObjectSet(String objectSetId)
    {
        JPAObjectSet jpaObjectSet = this.getEntityManager().find(JPAObjectSet.class, objectSetId);

        if(jpaObjectSet == null)
            return Optional.absent();

        return Optional.of(jpaObjectSet.toObjectSet());
    }

    /**
     * Gets the {@link InformationStoreDefinition} related to the
     * {@link ObjectMetadata}.
     *
     * @param objectMetadata The {@link ObjectMetadata} for which the query
     *                       will be run.
     *
     * @return The {@link InformationStoreDefinition} related to the given
     * {@link ObjectMetadata}.
     */
    @Override
    public InformationStoreDefinition getSourceInformationStoreDefinition(ObjectMetadata objectMetadata)
    {
        Optional<InformationStoreDefinition> isd = this.informationStores.get(objectMetadata.getInformationStoreId());

        if(!isd.isPresent())
            throw new SkyeException("The InformationStoreDefinition Id is invalid.");

        return isd.get();
    }

    /**
     * Gets the {@link ArchiveStoreDefinition} related to the
     * {@link ArchiveContentBlock}.
     *
     * @param archiveContentBlock The {@link ArchiveContentBlock} for which the
     *                            query will be run.
     *
     * @return The {@link ArchiveStoreDefinition} related to the given
     * {@link ArchiveContentBlock}.
     */
    @Override
    public ArchiveStoreDefinition getArchiveStoreDefinition(ArchiveContentBlock archiveContentBlock)
    {
        Optional<ArchiveStoreDefinition> asd = this.archiveStores.get(archiveContentBlock.getArchiveStoreDefinitionId());

        if(!asd.isPresent())
            throw new SkyeException("The ArchiveStoreDefinition Id is invalid.");

        return asd.get();
    }

    /**
     * Gets the {@link JPAObjectSet} which has the given id.
     *
     * @param objectSetId The id of the {@link ObjectSet} to be queried.
     *
     * @return An {@link Optional} wrapper containing the {@link ObjectSet},
     * if it is found.
     */
    protected Optional<JPAObjectSet> getJpaObjectSet(String objectSetId)
    {
        JPAObjectSet jpaObjectSet = this.getEntityManager().find(JPAObjectSet.class, objectSetId);

        if(jpaObjectSet == null)
            return Optional.absent();

        return Optional.of(jpaObjectSet);
    }
}
