package org.skye.resource.dao;

import org.hibernate.SessionFactory;
import org.skye.domain.User;

/**
 * DAO for the {@link org.skye.domain.User}
 */
public class UserDAO extends AbstractPaginatingDAO<User> {

    public UserDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
