package org.openskye.domain.dao;

import com.google.common.base.Optional;
import com.google.inject.Provider;
import io.dropwizard.util.Generics;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.SkyeSession;
import org.openskye.domain.AuditEvent;
import org.openskye.domain.AuditLog;
import org.openskye.domain.Identifiable;
import org.openskye.query.RequestQueryContext;
import org.openskye.query.RequestQueryContextHolder;
import org.openskye.query.SortDirection;

import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.validation.*;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * We are building up our own DAO to make it a little
 * more intelligent that the basic one since we will
 * need to include security and also pagination
 */
@Slf4j
public abstract class AbstractPaginatingDAO<T extends Identifiable> {
    @Inject
    private Provider<EntityManager> emf;
    @Inject
    private AuditLogDAO auditLogDAO;
    @Inject
    private SkyeSession skyeSession;
    private Class<T> entityClass = (Class<T>) Generics.getTypeParameter(getClass());
    // We wanted to have exceptions before commit in the DAO
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public Provider<EntityManager> getEntityManagerProvider() {
        return emf;
    }

    public PaginatedResult<T> list() {
        CriteriaQuery<T> criteria = createCriteriaQuery();
        Root<T> selectEntity = criteria.from(entityClass);

        criteria.select(selectEntity);
        RequestQueryContext requestContext = RequestQueryContextHolder.getContext();

        if (requestContext != null && requestContext.getSort() != null) {
            try {
                Path<Object> sortCol = selectEntity.get(requestContext.getSort());
                log.debug("Applying sort on " + sortCol);
                if (requestContext.getSortDir().equals(SortDirection.DESC)) {
                    log.debug("Sorting desc");
                    criteria.orderBy(getCriteriaBuilder().desc(sortCol));
                } else {
                    log.debug("Sorting asc");
                    criteria.orderBy(getCriteriaBuilder().asc(sortCol));
                }

            } catch (IllegalArgumentException e) {
                log.debug("Ignoring sort, column not found");
            }
        } else {
            // By default lets sort by id so that we are
            // at least a little deterministic
            criteria.orderBy(getCriteriaBuilder().asc(selectEntity.get("id")));
        }

        TypedQuery<T> query = currentEntityManager().createQuery(criteria);

        // Apply the pagination
        query.setFirstResult(requestContext.getOffset());
        query.setMaxResults(requestContext.getPageSize());

        return new PaginatedResult<>(query.getResultList());
    }

    public boolean isAudited() {
        return true;
    }

    protected CriteriaQuery<T> createCriteriaQuery() {
        return getCriteriaBuilder().createQuery(entityClass);
    }

    protected CriteriaBuilder getCriteriaBuilder() {
        return currentEntityManager().getCriteriaBuilder();
    }

    /**
     * Internal method used to ensure that we create an
     * {@link AuditLog} for changes made through this DAO
     *
     * @param newInstance The object to be audited
     * @param event       the event
     * @return The object that was audited
     */
    private T audit(T newInstance, AuditEvent event) {
        if (isAudited()) {
            AuditLog auditLog = new AuditLog();
            auditLog.setAuditEntity(entityClass.getSimpleName());
            auditLog.setAuditEvent(event);
            auditLog.setUser(skyeSession.getUser());
            auditLogDAO.create(auditLog);
        }
        return newInstance;
    }

    /**
     * Returns the current {@link EntityManager}.
     *
     * @return the current session
     */
    protected EntityManager currentEntityManager() {
        return emf.get();
    }

    /**
     * Creates a new, persistent instance of the domain object, based on
     * the newInstance given.  This method should check to ensure that
     * the domain object is not already found, and if it is, the method should
     * throw an exception.
     *
     * @param newInstance The instance to be created.
     * @return The created instance.
     * @throws EntityExistsException Indicates that the Entity to be created
     *                               would be a duplicate record.
     */
    public T create(T newInstance) {
        if (newInstance == null)
            throw new ValidationException();
        validate(newInstance);

        this.currentEntityManager().persist(newInstance);
        audit(newInstance, AuditEvent.INSERT);

        return newInstance;
    }

    private void validate(T newInstance) {
        Set<ConstraintViolation<T>> violations = validator.validate(newInstance);
        if (violations.size() > 0) {
            throw new ConstraintViolationException(violations);
        }
    }

    /**
     * Updates the instance found at the given id.  These methods
     * should check to ensure that the domain object already exists, and if
     * it does not, an exception should be thrown.
     *
     * @param id              The Id of the Entity to be updated.
     * @param updatedInstance The updated instance of the Entity.
     * @return The updated instance of the Entity.
     * @throws EntityNotFoundException Indicates that the Entity to be updated
     *                                 was not found, and should be created first.
     */
    public T update(String id, T updatedInstance) {
        if (updatedInstance == null)
            throw new ValidationException();

        if (this.get(id) == null)
            throw new EntityNotFoundException();

        if (!id.equals(updatedInstance.getId()))
            throw new ValidationException();
        validate(updatedInstance);

        this.currentEntityManager().merge(updatedInstance);
        audit(updatedInstance, AuditEvent.UPDATE);

        return updatedInstance;
    }

    @SuppressWarnings("unchecked")
    public Optional<T> get(String id) {
        T result = currentEntityManager().find(entityClass, checkNotNull(id));

        if (result == null)
            return Optional.absent();
        else {
            return Optional.of(result);
        }
    }

    @SuppressWarnings("unchecked")
    public boolean delete(String id) {
        T entity = currentEntityManager().find(entityClass, checkNotNull(id));
        if (entity != null) {
            currentEntityManager().remove(entity);
            audit(entity, AuditEvent.DELETE);
            return true;
        } else return false;
    }

    /**
     * Update the given instance of the object
     *
     * @param instance the instance to update
     */
    public void update(T instance) {
        update(instance.getId(), instance);
    }

    /**
     * Delete the given instance of the object
     *
     * @param instance
     */
    public void delete(T instance) {
        delete(instance.getId());
    }

    /**
     * Attempt to place a lock on a row
     *
     * @param instance the object representing the row to lock
     * @param mode     the mode of the requested lock
     */
    public void lock(T instance, LockModeType mode) {
        currentEntityManager().lock(instance, mode);
    }
}

