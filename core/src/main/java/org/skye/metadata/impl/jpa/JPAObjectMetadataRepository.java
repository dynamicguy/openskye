package org.skye.metadata.impl.jpa;

import com.google.common.base.Optional;
import org.skye.core.ArchiveContentBlock;
import org.skye.core.ObjectMetadata;
import org.skye.core.ObjectSet;
import org.skye.core.SkyeException;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.domain.InformationStoreDefinition;
import org.skye.domain.Task;
import org.skye.metadata.ObjectMetadataRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    @PersistenceContext
    protected EntityManager entityManager;

    @Override
    public ObjectSet createObjectSet(String name)
    {
        JPAObjectSet jpaObjectSet = new JPAObjectSet();

        jpaObjectSet.setName(name);

        this.entityManager.getTransaction().begin();

        try
        {
            this.entityManager.persist(jpaObjectSet);
            this.entityManager.getTransaction().commit();
        }
        catch(Exception ex)
        {
            this.entityManager.getTransaction().rollback();

            throw new SkyeException("The ObjectSet could not be created.", ex);
        }

        return jpaObjectSet.toObjectSet();
    }

    @Override
    public void deleteObjectSet(ObjectSet objectSet)
    {
        Optional<JPAObjectSet> jpaObjectSet = this.getJpaObjectSet(objectSet.getId());

        if(!jpaObjectSet.isPresent())
            throw new SkyeException("The ObjectSet does not exist, and cannot be deleted.");

        this.entityManager.getTransaction().begin();

        try
        {
            this.entityManager.remove(jpaObjectSet.get());
            this.entityManager.getTransaction().commit();
        }
        catch(Exception ex)
        {
            this.entityManager.getTransaction().rollback();

            throw new SkyeException("The ObjectSet could not be deleted.", ex);
        }

        return;
    }

    @Override
    public void addObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata)
    {
        Optional<JPAObjectSet> jpaObjectSet = this.getJpaObjectSet(objectSet.getId());
        JPAObjectMetadata jpaObjectMetadata = new JPAObjectMetadata(objectMetadata);

        if(!jpaObjectSet.isPresent())
            throw new SkyeException("The ObjectSet to which you are attempting to add does not exist.");

        if(this.isObjectInSet(objectSet, objectMetadata))
            throw new SkyeException("The ObjectSet already contains the given ObjectMetadata");

        this.entityManager.getTransaction().begin();

        try
        {
            jpaObjectSet.get().getObjectMetadataSet().add(jpaObjectMetadata);
            this.entityManager.merge(jpaObjectSet.get());
            this.entityManager.getTransaction().commit();
        }
        catch(Exception ex)
        {
            this.entityManager.getTransaction().rollback();

            throw new SkyeException("The ObjectMetadata could not be added to the ObjectSet.", ex);
        }

        return;
    }

    @Override
    public void removeObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata)
    {
        Optional<JPAObjectSet> jpaObjectSet = this.getJpaObjectSet(objectSet.getId());
        JPAObjectMetadata jpaObjectMetadata = new JPAObjectMetadata(objectMetadata);

        if(!jpaObjectSet.isPresent())
            throw new SkyeException("The ObjectSet from which you are attempting to remove does not exist.");

        if(!this.isObjectInSet(objectSet, objectMetadata))
            throw new SkyeException("The ObjectSet does not contain the given ObjectMetadata.");

        this.entityManager.getTransaction().begin();

        try
        {
            jpaObjectSet.get().getObjectMetadataSet().remove(jpaObjectMetadata);
            this.entityManager.merge(jpaObjectSet.get());
            this.entityManager.getTransaction().commit();
        }
        catch(Exception ex)
        {
            this.entityManager.getTransaction().rollback();

            throw new SkyeException("The ObjectMetadata count not be removed from the ObjectSet.", ex);
        }

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
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<JPAObjectSet> root = cq.from(JPAObjectSet.class);
        SetJoin<JPAObjectSet, JPAObjectMetadata> objectToMetadata = root.join(JPAObjectSet_.objectMetadataSet);

        cq.select(cb.count(root));
        cq.where(cb.equal(objectToMetadata.get(JPAObjectMetadata_.id), objectMetadata.getId()));

        if(this.entityManager.createQuery(cq).getSingleResult() > 0)
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
        JPAObjectMetadata jpaObjectMetadata = this.entityManager.find(JPAObjectMetadata.class, id);

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

        this.entityManager.getTransaction().begin();

        try
        {
            if(this.get(jpaObjectMetadata.getId()).isPresent())
                this.entityManager.merge(jpaObjectMetadata);
            else
                this.entityManager.persist(jpaObjectMetadata);

            this.entityManager.getTransaction().commit();
        }
        catch(Exception ex)
        {
            this.entityManager.getTransaction().rollback();

            throw new SkyeException("Failed to write the given ObjectMetadata with id " + objectMetadata.getId(), ex);
        }

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
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<JPAObjectMetadata> cq = cb.createQuery(JPAObjectMetadata.class);
        Root<JPAObjectMetadata> root = cq.from(JPAObjectMetadata.class);

        cq.select(root);
        cq.where(cb.equal(root.get(JPAObjectMetadata_.informationStoreDefinitionId), informationStoreDefinition.getId()));

        listJpaObjectMetadata = this.entityManager.createQuery(cq).getResultList();

        for(JPAObjectMetadata jpa : listJpaObjectMetadata)
            listObjectMetadata.add(jpa.toObjectMetadata());

        return listObjectMetadata;
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(Task task)
    {
        List<ObjectMetadata> listObjectMetadata = new ArrayList<>();
        List<JPAObjectMetadata> listJpaObjectMetadata = null;
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<JPAObjectMetadata> cq = cb.createQuery(JPAObjectMetadata.class);
        Root<JPAObjectMetadata> root = cq.from(JPAObjectMetadata.class);

        cq.select(root);
        cq.where(cb.equal(root.get(JPAObjectMetadata_.taskId), task.getId()));

        listJpaObjectMetadata = this.entityManager.createQuery(cq).getResultList();

        for(JPAObjectMetadata jpa : listJpaObjectMetadata)
            listObjectMetadata.add(jpa.toObjectMetadata());

        return listObjectMetadata;

    }

    @Override
    public Iterable<ObjectMetadata> getObjects(ObjectSet objectSet) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<ObjectSet> getObjectSet(String objectSetId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InformationStoreDefinition getSourceInformationStoreDefinition(ObjectMetadata objectMetadata) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArchiveStoreDefinition getArchiveStoreDefinition(ArchiveContentBlock archiveContentBlock) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected Optional<JPAObjectSet> getJpaObjectSet(String objectSetId)
    {
        JPAObjectSet jpaObjectSet = this.entityManager.find(JPAObjectSet.class, objectSetId);

        if(jpaObjectSet == null)
            return Optional.absent();

        return Optional.of(jpaObjectSet);
    }
}
