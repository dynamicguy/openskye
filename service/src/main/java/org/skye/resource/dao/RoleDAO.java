package org.skye.resource.dao;

import org.hibernate.SessionFactory;
import org.skye.domain.Role;

/**
 * DAO for the {@link org.skye.domain.Role}
 */
public class RoleDAO extends AbstractPaginatingDAO<Role> {

    public RoleDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
