package org.skye.resource.dao;

import com.yammer.dropwizard.util.Generics;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.skye.util.PaginatedResult;

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
     * Returns the current {@link org.hibernate.Session}.
     *
     * @return the current session
     */
    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }
}
