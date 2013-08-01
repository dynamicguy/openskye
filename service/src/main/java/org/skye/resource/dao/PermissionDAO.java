package org.skye.resource.dao;

import org.hibernate.SessionFactory;
import org.skye.domain.Permission;

/**
 * DAO for the {@link org.skye.domain.Permission}
 */
public class PermissionDAO extends AbstractPaginatingDAO<Permission> {

    public PermissionDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
