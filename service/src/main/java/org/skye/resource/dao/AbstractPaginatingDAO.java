package org.skye.resource.dao;

import com.yammer.dropwizard.util.Generics;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.skye.util.PaginatedResult;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * We are building up our own DAO to make it a little
 * more intelligent that the basic one since we will
 * need to include security and also pagination
 */
public class AbstractPaginatingDAO<T> {

    private final SessionFactory sessionFactory;
    private final Class<?> entityClass;

    /**
     * Creates a new DAO with a given session provider.
     *
     * @param sessionFactory a session provider
     */
    public AbstractPaginatingDAO(SessionFactory sessionFactory) {
        this.sessionFactory = checkNotNull(sessionFactory);
        this.entityClass = Generics.getTypeParameter(getClass());
    }

    public PaginatedResult<T> list() {
        PaginatedResult<T> result = new PaginatedResult<>();
        result.setResults(criteria().list());
        return result;
    }

    /**
     * Creates a new {@link org.hibernate.Criteria} query for {@code <E>}.
     *
     * @return a new {@link org.hibernate.Criteria} query
     * @see Session#createCriteria(Class)
     */
    protected Criteria criteria() {
        return currentSession().createCriteria(entityClass);
    }

    /**
     * Persists the instance
     *
     * @param newInstance
     * @return
     */
    public T persist(T newInstance) {
        currentSession().persist(newInstance);
        return newInstance;
    }

    /**
     * Returns the current {@link org.hibernate.Session}.
     *
     * @return the current session
     */
    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
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
    public T get(String id) {
        return (T) currentSession().get(entityClass, checkNotNull(id));
    }

    public void delete(String id) {
        currentSession().delete(currentSession().get(entityClass, checkNotNull(id)));
    }
}
