package org.skye.metadata.impl.jpa;

import com.google.common.base.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import org.skye.core.ObjectMetadata;
import org.skye.core.ObjectSet;
import org.skye.domain.InformationStoreDefinition;
import org.skye.domain.Task;
import org.skye.metadata.ObjectMetadataRepository;
import org.skye.core.SkyeException;
import org.skye.stores.StoreRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the {@link ObjectMetadataRepository} using the Java
 * Persistence API (JPA) to act on database systems.
 */
public class JPAObjectMetadataRepository implements ObjectMetadataRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    protected StoreRegistry storeRegistry;

    /**
     * Accessor for the {@link EntityManager} in the case that the object is
     * inherited.
     *
     * @return The injected {@link EntityManager} for the instance.
     */
    protected EntityManager getEntityManager()
    {
        return this.entityManager;
    }

    @Override
    public ObjectSet createObjectSet()
    {
        return null;
    }

    @Override
    public void deleteObjectSet(ObjectSet objectSet)
    {
        return;
    }

    @Override
    public void addObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        //To change body of implemented methods use File | Settings | File Templates.
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

        return Optional.of(jpaObjectMetadata.ToObjectMetadata());
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
    public void put(ObjectMetadata objectMetadata) throws SkyeException
    {
        JPAObjectMetadata jpaObjectMetadata = new JPAObjectMetadata(objectMetadata);
        EntityManager manager = this.getEntityManager();

        manager.getTransaction().begin();

        try
        {
            if(this.get(jpaObjectMetadata.getId()).isPresent())
                manager.remove(jpaObjectMetadata);

            manager.persist(jpaObjectMetadata);

            manager.getTransaction().commit();
        }
        catch(Exception ex)
        {
            manager.getTransaction().rollback();

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
        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<JPAObjectMetadata> cq = cb.createQuery(JPAObjectMetadata.class);
        Root<JPAObjectMetadata> root = cq.from(JPAObjectMetadata.class);
        TypedQuery<JPAObjectMetadata> q;

        cq.select(root);
        cq.where(cb.equal(root.get("informationStore"), informationStoreDefinition));

        q = this.getEntityManager().createQuery(cq);

        listJpaObjectMetadata = q.getResultList();

        for(JPAObjectMetadata jpa : listJpaObjectMetadata)
            listObjectMetadata.add(jpa.ToObjectMetadata());

        return listObjectMetadata;
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(Task task)
    {
        List<ObjectMetadata> listObjectMetadata = new ArrayList<>();
        List<JPAObjectMetadata> listJpaObjectMetadata = null;
        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<JPAObjectMetadata> cq = cb.createQuery(JPAObjectMetadata.class);
        Root<JPAObjectMetadata> root = cq.from(JPAObjectMetadata.class);

        cq.select(root);
        cq.where(cb.equal(root.get("taskId"), task.getId()));

        listJpaObjectMetadata = this.getEntityManager().createQuery(cq).getResultList();

        for(JPAObjectMetadata jpa : listJpaObjectMetadata)
            listObjectMetadata.add(jpa.ToObjectMetadata());

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
}
