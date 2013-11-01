package org.openskye.metadata.impl.jpa;

import com.google.common.base.Optional;
import com.google.inject.Provider;
import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.ObjectSet;
import org.openskye.core.SkyeException;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.Task;
import org.openskye.domain.dao.ArchiveStoreDefinitionDAO;
import org.openskye.domain.dao.InformationStoreDefinitionDAO;
import org.openskye.metadata.ObjectMetadataRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
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
public class JPAObjectMetadataRepository implements ObjectMetadataRepository {
    @Inject
    protected ArchiveStoreDefinitionDAO archiveStores;
    @Inject
    protected InformationStoreDefinitionDAO informationStores;
    @Inject
    private Provider<EntityManager> emf;

    /**
     * Gets the {@link EntityManager} from the Guice-based Provider.
     *
     * @return The injected {@link EntityManager}.
     */
    protected EntityManager getEntityManager() {
        return emf.get();
    }

    /**
     * Creates a new {@link ObjectSet}, which is empty and can be used to
     * reference a set of {@link ObjectMetadata} stored on disk.
     *
     * @param name The name to be used for the {@link ObjectSet}.
     * @return The new {@link ObjectSet}.
     */
    @Override
    public ObjectSet createObjectSet(String name) {
        JPAObjectSet jpaObjectSet = new JPAObjectSet();
        jpaObjectSet.setName(name);
        getEntityManager().persist(jpaObjectSet);

        return jpaObjectSet.toObjectSet();
    }

    /**
     * Deletes an existing {@link ObjectSet}, if it exists, or throws a
     * {@link SkyeException} if it does not.
     *
     * @param objectSet the object set to remove
     */
    @Override
    public void deleteObjectSet(ObjectSet objectSet) {
        Optional<JPAObjectSet> jpaObjectSet = getJpaObjectSet(objectSet.getId());

        if (!jpaObjectSet.isPresent())
            throw new EntityNotFoundException();

        getEntityManager().remove(jpaObjectSet.get());
    }

    /**
     * Adds an {@link ObjectMetadata} instance to the {@link ObjectSet}.
     *
     * @param objectSet      the object set
     * @param objectMetadata the object metadata to add
     */
    @Override
    public void addObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        Optional<JPAObjectSet> jpaObjectSet = getJpaObjectSet(objectSet.getId());
        JPAObjectMetadata jpaObjectMetadata = new JPAObjectMetadata(objectMetadata);

        if (!jpaObjectSet.isPresent())
            throw new EntityNotFoundException();

        if (!isObjectInSet(objectSet, objectMetadata)) {
            jpaObjectSet.get().getObjectMetadataSet().add(jpaObjectMetadata);
            getEntityManager().merge(jpaObjectSet.get());
        }
    }

    /**
     * Removes an {@link ObjectMetadata} instance from the {@link ObjectSet}.
     *
     * @param objectSet      the object set
     * @param objectMetadata the object metadata to remove
     */
    @Override
    public void removeObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        Optional<JPAObjectSet> jpaObjectSet = getJpaObjectSet(objectSet.getId());
        JPAObjectMetadata jpaObjectMetadata = new JPAObjectMetadata(objectMetadata);

        if (!jpaObjectSet.isPresent())
            throw new EntityNotFoundException();

        if (!isObjectInSet(objectSet, objectMetadata))
            throw new SkyeException("The ObjectSet does not contain the given ObjectMetadata.");

        jpaObjectSet.get().getObjectMetadataSet().remove(jpaObjectMetadata);
        getEntityManager().merge(jpaObjectSet.get());
    }

    /**
     * Checks to see if the {@link ObjectMetadata} is found in the given
     * {@link ObjectSet}.
     *
     * @param objectSet      The {@link ObjectSet} against which the query will run.
     * @param objectMetadata The {@link ObjectMetadata} for which the query is
     *                       run.
     * @return True if the {@link ObjectMetadata} is found in the
     *         {@link ObjectSet}, or false if it is not found.
     */
    @Override
    public boolean isObjectInSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<JPAObjectSet> root = cq.from(JPAObjectSet.class);
        SetJoin<JPAObjectSet, JPAObjectMetadata> objectToMetadata = root.join(JPAObjectSet_.objectMetadataSet);

        cq.select(cb.count(root));
        cq.where(cb.equal(objectToMetadata.get(JPAObjectMetadata_.id), objectMetadata.getId()));

        return (getEntityManager().createQuery(cq).getSingleResult() > 0);
    }

    /**
     * Gets the {@link @ObjectMetadata} associated with the given id.
     *
     * @param id The identifier of the object
     * @return An Optional wrapper which will include the requested
     *         {@link ObjectMetadata}, if it is found.
     */
    @Override
    public Optional<ObjectMetadata> get(String id) {
        JPAObjectMetadata jpaObjectMetadata = getEntityManager().find(JPAObjectMetadata.class, id);

        if (jpaObjectMetadata == null)
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
    public ObjectMetadata put(ObjectMetadata objectMetadata) {
        JPAObjectMetadata jpaObjectMetadata = new JPAObjectMetadata(objectMetadata);
        getEntityManager().merge(jpaObjectMetadata);
        return jpaObjectMetadata.toObjectMetadata();
    }

    /**
     * Based on an {@link InformationStoreDefinition}, return a collection of
     * {@link ObjectMetadata} which use the described
     * {@link org.openskye.core.InformationStore}.
     *
     * @param informationStoreDefinition The {@link InformationStoreDefinition}
     *                                   which describes the information store.
     * @return An {@link Iterable} collection of {@link ObjectMetadata} based
     *         on the given {@link InformationStoreDefinition}.
     */
    @Override
    public Iterable<ObjectMetadata> getObjects(InformationStoreDefinition informationStoreDefinition) {
        List<ObjectMetadata> listObjectMetadata = new ArrayList<>();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<JPAObjectMetadata> cq = cb.createQuery(JPAObjectMetadata.class);
        Root<JPAObjectMetadata> root = cq.from(JPAObjectMetadata.class);

        cq.select(root);

        cq.where(cb.equal(root.get(JPAObjectMetadata_.informationStoreId), informationStoreDefinition.getId()));

        List<JPAObjectMetadata> listJpaObjectMetadata = getEntityManager().createQuery(cq).getResultList();

        for (JPAObjectMetadata jpa : listJpaObjectMetadata)
            listObjectMetadata.add(jpa.toObjectMetadata());

        return listObjectMetadata;
    }

    /**
     * Gets a collection of all of the {@link ObjectMetadata} related to a
     * given {@link Task}.
     *
     * @param task The {@link Task} for which the query will run.
     * @return An {@link Iterable} collection containing all of the
     *         {@link ObjectMetadata} for the given {@link Task}.
     */
    @Override
    public Iterable<ObjectMetadata> getObjects(Task task) {
        List<ObjectMetadata> listObjectMetadata = new ArrayList<>();
        List<JPAObjectMetadata> listJpaObjectMetadata = null;
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<JPAObjectMetadata> cq = cb.createQuery(JPAObjectMetadata.class);
        Root<JPAObjectMetadata> root = cq.from(JPAObjectMetadata.class);

        cq.select(root);
        cq.where(cb.equal(root.get(JPAObjectMetadata_.taskId), task.getId()));

        listJpaObjectMetadata = getEntityManager().createQuery(cq).getResultList();

        for (JPAObjectMetadata jpa : listJpaObjectMetadata)
            listObjectMetadata.add(jpa.toObjectMetadata());

        return listObjectMetadata;

    }

    /**
     * Gets a collection containing all {@link ObjectMetadata} found in the
     * {@link ObjectSet}.
     *
     * @param objectSet The {@link ObjectSet} for which the query will be run.
     * @return An {@link Iterable} collection containing all of the
     *         {@link ObjectMetadata} in the given {@link ObjectSet}.
     */
    @Override
    public Iterable<ObjectMetadata> getObjects(ObjectSet objectSet) {
        List<ObjectMetadata> listObjectMetadata = new ArrayList<>();
        JPAObjectSet jpaObjectSet = null;
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<JPAObjectSet> cq = cb.createQuery(JPAObjectSet.class);
        Root<JPAObjectSet> root = cq.from(JPAObjectSet.class);

        cq.select(root);
        cq.where(cb.equal(root.get(JPAObjectSet_.id), objectSet.getId()));

        jpaObjectSet = getEntityManager().createQuery(cq).getSingleResult();

        for (JPAObjectMetadata jpaObjectMetadata : jpaObjectSet.getObjectMetadataSet())
            listObjectMetadata.add(jpaObjectMetadata.toObjectMetadata());

        return listObjectMetadata;
    }

    /**
     * Gets the {@link ObjectSet} which has the given id.
     *
     * @param objectSetId the id of the object set for lookup
     * @return An {@link Optional} wrapper containing the {@link ObjectSet},
     *         if it is found.
     */
    @Override
    public Optional<ObjectSet> getObjectSet(String objectSetId) {
        JPAObjectSet jpaObjectSet = getEntityManager().find(JPAObjectSet.class, objectSetId);

        if (jpaObjectSet == null)
            return Optional.absent();

        return Optional.of(jpaObjectSet.toObjectSet());
    }

    @Override
    public Iterable<ObjectSet> getAllObjectSets() {
        List<JPAObjectSet> listJpaObjectSets;
        List<ObjectSet> listObjectSets = new ArrayList<>();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<JPAObjectSet> cq = cb.createQuery(JPAObjectSet.class);
        Root<JPAObjectSet> root = cq.from(JPAObjectSet.class);

        cq.select(root);

        listJpaObjectSets = getEntityManager().createQuery(cq).getResultList();

        for (JPAObjectSet jpa : listJpaObjectSets)
            listObjectSets.add(jpa.toObjectSet());

        return listObjectSets;
    }

    public Iterable<ObjectMetadata> getAllObjects() {
        List<JPAObjectMetadata> listJpaObjectMetadata;
        List<ObjectMetadata> listObjectMetadata = new ArrayList<>();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<JPAObjectMetadata> cq = cb.createQuery(JPAObjectMetadata.class);
        Root<JPAObjectMetadata> root = cq.from(JPAObjectMetadata.class);

        cq.select(root);

        listJpaObjectMetadata = getEntityManager().createQuery(cq).getResultList();

        for (JPAObjectMetadata jpa : listJpaObjectMetadata)
            listObjectMetadata.add(jpa.toObjectMetadata());

        return listObjectMetadata;
    }

    /**
     * Gets the {@link InformationStoreDefinition} related to the
     * {@link ObjectMetadata}.
     *
     * @param objectMetadata The {@link ObjectMetadata} for which the query
     *                       will be run.
     * @return The {@link InformationStoreDefinition} related to the given
     *         {@link ObjectMetadata}.
     */
    @Override
    public InformationStoreDefinition getSourceInformationStoreDefinition(ObjectMetadata objectMetadata) {
        Optional<InformationStoreDefinition> isd = informationStores.get(objectMetadata.getInformationStoreId());

        if (!isd.isPresent())
            throw new SkyeException("The InformationStoreDefinition Id is invalid.");

        return isd.get();
    }

    /**
     * Gets the {@link ArchiveStoreDefinition} related to the
     * {@link ArchiveContentBlock}.
     *
     * @param archiveContentBlock The {@link ArchiveContentBlock} for which the
     *                            query will be run.
     * @return The {@link ArchiveStoreDefinition} related to the given
     *         {@link ArchiveContentBlock}.
     */
    @Override
    public ArchiveStoreDefinition getArchiveStoreDefinition(ArchiveContentBlock archiveContentBlock) {
        Optional<ArchiveStoreDefinition> asd = archiveStores.get(archiveContentBlock.getArchiveStoreDefinitionId());

        if (!asd.isPresent())
            throw new SkyeException("The ArchiveStoreDefinition Id is invalid.");

        return asd.get();
    }

    /**
     * Gets the {@link JPAObjectSet} which has the given id.
     *
     * @param objectSetId The id of the {@link ObjectSet} to be queried.
     * @return An {@link Optional} wrapper containing the {@link ObjectSet},
     *         if it is found.
     */
    protected Optional<JPAObjectSet> getJpaObjectSet(String objectSetId) {
        JPAObjectSet jpaObjectSet = getEntityManager().find(JPAObjectSet.class, objectSetId);

        if (jpaObjectSet == null)
            return Optional.absent();

        return Optional.of(jpaObjectSet);
    }

}
