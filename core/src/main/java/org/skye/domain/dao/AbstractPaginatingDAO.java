package org.skye.domain.dao;

import com.google.common.base.Optional;
import com.google.inject.Provider;
import io.dropwizard.util.Generics;
import org.eclipse.persistence.exceptions.ValidationException;
import org.skye.domain.Identifiable;

import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * We are building up our own DAO to make it a little
 * more intelligent that the basic one since we will
 * need to include security and also pagination
 */
public abstract class AbstractPaginatingDAO<T extends Identifiable> {
    @Inject
    private Provider<EntityManager> emf;
    private Class<T> entityClass = (Class<T>) Generics.getTypeParameter(getClass());

    public PaginatedResult<T> list() {
        PaginatedResult<T> result = new PaginatedResult<>();
        CriteriaQuery<T> criteria = createCriteriaQuery();
        Root<T> selectEntity = criteria.from(entityClass);
        criteria.select(selectEntity);
        result.setResults(currentEntityManager().createQuery(criteria).getResultList());
        return result;
    }

    protected CriteriaQuery<T> createCriteriaQuery() {
        return createCriteriaBuilder().createQuery(entityClass);
    }

    protected CriteriaBuilder createCriteriaBuilder() {
        return currentEntityManager().getCriteriaBuilder();
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

        this.currentEntityManager().persist(newInstance);

        return newInstance;
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

        if (id != updatedInstance.getId())
            throw new ValidationException();

        this.currentEntityManager().persist(updatedInstance);

        return updatedInstance;
    }

    @SuppressWarnings("unchecked")
    public Optional<T> get(String id) {
        T result = (T) currentEntityManager().find(entityClass, checkNotNull(id));
        if (result == null)
            return Optional.absent();
        else
            return Optional.of(result);
    }

    public boolean delete(String id) {
        Object entity = currentEntityManager().find(entityClass, checkNotNull(id));
        if (entity != null) {
            currentEntityManager().remove(entity);
            return true;
        } else return false;
    }
}
