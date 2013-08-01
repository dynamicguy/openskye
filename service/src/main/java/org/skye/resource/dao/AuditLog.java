package org.skye.resource.dao;

import org.hibernate.SessionFactory;
import org.skye.domain.Role;

/**
 * DAO for the {@link org.skye.domain.AuditLog}
 */
public class AuditLog extends AbstractPaginatingDAO<AuditLog> {

    public AuditLog(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
