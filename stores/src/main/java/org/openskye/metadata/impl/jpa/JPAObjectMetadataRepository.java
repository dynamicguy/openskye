package org.openskye.metadata.impl.jpa;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.openskye.core.*;
import org.openskye.domain.*;
import org.openskye.domain.dao.ArchiveStoreInstanceDAO;
import org.openskye.domain.dao.InformationStoreDefinitionDAO;
import org.openskye.domain.dao.ProjectDAO;
import org.openskye.metadata.ObjectMetadataRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * An implementation of the {@link ObjectMetadataRepository} using the Java
 * Persistence API (JPA) to act on database systems.
 */
@Slf4j
public class JPAObjectMetadataRepository implements ObjectMetadataRepository {
    @Inject
    protected ArchiveStoreInstanceDAO archiveStores;
    @Inject
    protected InformationStoreDefinitionDAO informationStores;
    @Inject
    protected ProjectDAO projects;

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
        log.debug("Creating object set with name " + name);
        ObjectSet objectSet = new ObjectSet();
        objectSet.setName(name);
        getEntityManager().persist(objectSet);
        log.debug("Created object set " + objectSet);
        return objectSet;
    }

    /**
     * Deletes an existing {@link ObjectSet}, if it exists, or throws a
     * {@link SkyeException} if it does not.
     *
     * @param objectSet the object set to remove
     */
    @Override
    public void deleteObjectSet(ObjectSet objectSet) {
        log.debug("Deleting object set " + objectSet);
        Optional<ObjectSet> foundObjectSet = getObjectSet(objectSet.getId());

        if (!foundObjectSet.isPresent())
            throw new EntityNotFoundException();

        getEntityManager().remove(foundObjectSet.get());
    }

    @Override
    public Optional<ArchiveContentBlock> getArchiveContentBlock(String checksum, long originalSize) {

        // TODO we need to implement this
        return Optional.absent();
    }

    /**
     * Adds an {@link ObjectMetadata} instance to the {@link ObjectSet}.
     *
     * @param objectSet      the object set
     * @param objectMetadata the object metadata to add
     */
    @Override
    public void addObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        log.debug("Adding object " + objectMetadata + " to object set " + objectSet);
        Optional<ObjectSet> foundObjectSet = getObjectSet(objectSet.getId());

        if (!foundObjectSet.isPresent())
            throw new EntityNotFoundException();

        if (!isObjectInSet(objectSet, objectMetadata)) {
            foundObjectSet.get().getObjectMetadataSet().add(objectMetadata);
            getEntityManager().merge(foundObjectSet.get());
        }
    }

    /**
     * Removes an {@link ObjectMetadata} instance from the {@link ObjectSet}.
     *
     * @param objectSet      the object set
     * @param objectMetadata the object metadata to remove
     */
    @Override
    public void removeObjectFromSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        log.debug("Removing object " + objectMetadata + " to object set " + objectSet);
        if (isObjectInSet(objectSet, objectMetadata))
            objectSet.getObjectMetadataSet().remove(objectMetadata);

        getEntityManager().merge(objectSet);
    }

    /**
     * Checks to see if the {@link ObjectMetadata} is found in the given
     * {@link ObjectSet}.
     *
     * @param objectSet      The {@link ObjectSet} against which the query will run.
     * @param objectMetadata The {@link ObjectMetadata} for which the query is
     *                       run.
     * @return True if the {@link ObjectMetadata} is found in the
     * {@link ObjectSet}, or false if it is not found.
     */
    @Override
    public boolean isObjectInSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ObjectSet> root = cq.from(ObjectSet.class);
        SetJoin<ObjectSet, ObjectMetadata> objectToMetadata = root.join(ObjectSet_.objectMetadataSet);

        cq.select(cb.count(root));
        cq.where(cb.equal(objectToMetadata.get(ObjectMetadata_.id), objectMetadata.getId()));

        return (getEntityManager().createQuery(cq).getSingleResult() > 0);
    }

    @Override
    public boolean isObjectInOMR(ObjectMetadata objectMetadata) {
        String queryPath = objectMetadata.getPath();
        DateTime queryLastModified = objectMetadata.getLastModified().minusSeconds(1); // ignore time difference less than 1 second
        String queryInformationStoreId = objectMetadata.getInformationStoreId();

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ObjectMetadata> root = cq.from(ObjectMetadata.class);

        cq.where( cb.equal(root.get(ObjectMetadata_.path), queryPath),
                cb.greaterThanOrEqualTo(root.get(ObjectMetadata_.lastModified), queryLastModified),
                cb.equal(root.get(ObjectMetadata_.informationStoreId), queryInformationStoreId) );

        cq.select(cb.count(root));

        return (getEntityManager().createQuery(cq).getSingleResult() > 0);
    }

    /**
     * Gets the {@link @ObjectMetadata} associated with the given id.
     *
     * @param id The identifier of the object
     * @return An Optional wrapper which will include the requested
     * {@link ObjectMetadata}, if it is found.
     */
    @Override
    public Optional<ObjectMetadata> get(String id) {

        ObjectMetadata jpaObjectMetadata = getEntityManager().find(ObjectMetadata.class, id);
        log.debug("Looking up object metadata for " + id + " has " + jpaObjectMetadata);
        return (jpaObjectMetadata != null ? Optional.of(jpaObjectMetadata) : Optional.<ObjectMetadata>absent());
    }

    /**
     * Either adds the {@link ObjectMetadata} to the repository, or, if it
     * already exists (by its id property), removes the old
     * {@link ObjectMetadata} and replaces it with the new one.  If an
     * Exception occurs, then the repository is rolled back to it's previous
     * state, and the exception should be rethrown.
     *
     * @param objectMetadata The {@link ObjectMetadata} to be added to the
     *                       repository.
     */
    @Override
    public ObjectMetadata put(ObjectMetadata objectMetadata) {
        log.debug("Putting object metadata " + objectMetadata);
        ObjectMetadata result = getEntityManager().merge(objectMetadata);
        return result;
    }

    /**
     * Based on an {@link InformationStoreDefinition}, return a collection of
     * {@link ObjectMetadata} which use the described
     * {@link org.openskye.core.InformationStore}.
     *
     * @param informationStoreDefinition The {@link InformationStoreDefinition}
     *                                   which describes the information store.
     * @return An {@link Iterable} collection of {@link ObjectMetadata} based
     * on the given {@link InformationStoreDefinition}.
     */
    @Override
    public Iterable<ObjectMetadata> getObjects(InformationStoreDefinition informationStoreDefinition) {
        List<ObjectMetadata> listObjectMetadata = null;
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ObjectMetadata> cq = cb.createQuery(ObjectMetadata.class);
        Root<ObjectMetadata> root = cq.from(ObjectMetadata.class);

        cq.select(root);

        cq.where(cb.equal(root.get(ObjectMetadata_.informationStoreId), informationStoreDefinition.getId()));

        // TODO we need to make this iterable or we will load everything into memory
        listObjectMetadata = getEntityManager().createQuery(cq).getResultList();

        // Make sure objects aren't stale
        for (Object next : listObjectMetadata) {
            getEntityManager().refresh(next);
        }

        return listObjectMetadata;
    }

    /**
     * Based on an {@link Project}, return a collection of
     * {@link ObjectMetadata} which use the described
     * {@link org.openskye.domain.Project}.
     *
     * @param project The {@link Project} which describes the information store.
     * @return An {@link Iterable} collection of {@link ObjectMetadata} based
     * on the given {@link Project}.
     */
    @Override
    public Iterable<ObjectMetadata> getObjects(Project project) {
        TypedQuery<ObjectMetadata> query = getEntityManager().createQuery("SELECT om FROM ObjectMetadata om " +
                "JOIN om.project p " +
                "where p = :project", ObjectMetadata.class);

        return query.setParameter("project", project).getResultList();
    }

    /**
     * Gets a collection of all of the {@link ObjectMetadata} related to a
     * given {@link Task}.
     *
     * @param task The {@link Task} for which the query will run.
     * @return An {@link Iterable} collection containing all of the
     * {@link ObjectMetadata} for the given {@link Task}.
     */
    @Override
    public Iterable<ObjectMetadata> getObjects(Task task) {
        List<ObjectMetadata> listObjectMetadata = null;
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ObjectMetadata> cq = cb.createQuery(ObjectMetadata.class);
        Root<ObjectMetadata> root = cq.from(ObjectMetadata.class);

        cq.select(root);
        cq.where(cb.equal(root.get(ObjectMetadata_.taskId), task.getId()));

        // TODO we need to make this iterable or we will load everything into memory
        listObjectMetadata = getEntityManager().createQuery(cq).getResultList();

        return listObjectMetadata;

    }

    /**
     * Gets a collection containing all {@link ObjectMetadata} found in the
     * {@link ObjectSet}.
     *
     * @param objectSet The {@link ObjectSet} for which the query will be run.
     * @return An {@link Iterable} collection containing all of the
     * {@link ObjectMetadata} in the given {@link ObjectSet}.
     */
    @Override
    public Iterable<ObjectMetadata> getObjects(ObjectSet objectSet) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ObjectSet> cq = cb.createQuery(ObjectSet.class);
        Root<ObjectSet> root = cq.from(ObjectSet.class);

        cq.select(root);
        cq.where(cb.equal(root.get(ObjectSet_.id), objectSet.getId()));

        // TODO we need to make this iterable or we will load everything into memory
        objectSet = getEntityManager().createQuery(cq).getSingleResult();

        Set<ObjectMetadata> omSet = objectSet.getObjectMetadataSet();
        // Make sure objects aren't stale
        for (Object next : omSet) {
            getEntityManager().refresh(next);
        }

        return omSet;
    }

    /**
     * Gets the {@link ObjectSet} which has the given id.
     *
     * @param objectSetId the id of the object set for lookup
     * @return An {@link Optional} wrapper containing the {@link ObjectSet},
     * if it is found.
     */
    @Override
    public Optional<ObjectSet> getObjectSet(String objectSetId) {
        ObjectSet objectSet = getEntityManager().find(ObjectSet.class, objectSetId);
        return (objectSet != null ? Optional.of(objectSet) : Optional.<ObjectSet>absent());
    }

    @Override
    public Iterable<ObjectSet> getAllObjectSets() {
        List<ObjectSet> listObjectSets = new ArrayList<>();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ObjectSet> cq = cb.createQuery(ObjectSet.class);
        Root<ObjectSet> root = cq.from(ObjectSet.class);

        cq.select(root);

        // TODO we need to make this iterable or we will load everything into memory
        listObjectSets = getEntityManager().createQuery(cq).getResultList();

        return listObjectSets;
    }

    public Iterable<ObjectMetadata> getAllObjects() {
        List<ObjectMetadata> listObjectMetadata = null;
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ObjectMetadata> cq = cb.createQuery(ObjectMetadata.class);
        Root<ObjectMetadata> root = cq.from(ObjectMetadata.class);

        cq.select(root);

        // TODO we need to make this iterable or we will load everything into memory
        listObjectMetadata = getEntityManager().createQuery(cq).getResultList();

        return listObjectMetadata;
    }

    /**
     * Gets the {@link InformationStoreDefinition} related to the
     * {@link ObjectMetadata}.
     *
     * @param objectMetadata The {@link ObjectMetadata} for which the query
     *                       will be run.
     * @return The {@link InformationStoreDefinition} related to the given
     * {@link ObjectMetadata}.
     */
    @Override
    public InformationStoreDefinition getSourceInformationStoreDefinition(ObjectMetadata objectMetadata) {
        Optional<InformationStoreDefinition> isd = informationStores.get(objectMetadata.getInformationStoreId());

        if (!isd.isPresent())
            throw new SkyeException("The InformationStoreDefinition Id is invalid.");

        return isd.get();
    }

    @Override
    public void updateObjectSet(Optional<ObjectSet> objectSet) {
        log.debug("Updating object set " + objectSet);
        getEntityManager().merge(objectSet);
    }

    @Override
    public Iterable<ArchiveContentBlock> getACBsForReplication(Node primary, Node target, Project archiveStoreInstance) {
        TypedQuery<ArchiveContentBlock> query = getEntityManager().createQuery("select a from ArchiveContentBlock a INNER JOIN a.nodes n where n.id=:primaryNodeId AND a.id NOT IN (SELECT a2.id FROM ArchiveContentBlock a2 INNER JOIN a2.nodes n2 WHERE n2.id=:targetNodeId)", ArchiveContentBlock.class);
        query.setParameter("primaryNodeId", primary.getId());
        query.setParameter("targetNodeId", target.getId());
        return query.getResultList();
    }

    @Override
    public Iterable<ArchiveContentBlock> getACBs(Project project, ArchiveStoreInstance asi) {
        TypedQuery<ArchiveContentBlock> query = getEntityManager().createQuery("select a from ArchiveContentBlock a where (a.project.id=:projectId AND a.archiveStoreInstance.id=:asiId)", ArchiveContentBlock.class);
        query.setParameter("asiId", asi.getId());
        query.setParameter("projectId", project.getId());
        return query.getResultList();
    }

    @Override
    public ArchiveContentBlock put(ArchiveContentBlock acb) {
        log.debug("Putting ACB " + acb);
        ArchiveContentBlock result = getEntityManager().merge(acb);
        return result;
    }
}
