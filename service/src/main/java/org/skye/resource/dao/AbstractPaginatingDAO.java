package org.skye.resource.dao;

import com.google.common.base.Optional;
import com.google.inject.Provider;
import com.yammer.dropwizard.util.Generics;
import org.hibernate.Session;
import org.skye.util.BadRequestException;
import org.skye.util.PaginatedResult;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * We are building up our own DAO to make it a little
 * more intelligent that the basic one since we will
 * need to include security and also pagination
 */
public abstract class AbstractPaginatingDAO<T> {

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
     * Persists the instance
     *
     * @param newInstance
     * @return
     */
    public T persist(T newInstance) {
        if (newInstance!=null)
            currentEntityManager().persist(newInstance);
        else throw new BadRequestException();
        return newInstance;
    }

    /**
     * Returns the current {@link org.hibernate.Session}.
     *
     * @return the current session
     */
    protected EntityManager currentEntityManager() {
        return emf.get();
    }

    /**
     * Return the persistent instance of {@code <E>} with the given identifier, or {@code null} if
     * there is no such persistent instance. (If the instance, or a proxy for the instance, is
     * already associated with the session, return that instance or proxy.)
     *
     * @param id an identifier
     * @return a persistent instance or {@code null}
     * @throws org.hibernate.HibernateException
     *
     * @see Session#get(Class, Serializable)
     */
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
