package org.skye.resource.dao;

import org.hibernate.SessionFactory;
import org.skye.domain.Domain;

/**
 * DAO for the {@link Domain}
 */
public class DomainDAO extends AbstractPaginatingDAO<Domain> {

    public DomainDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
