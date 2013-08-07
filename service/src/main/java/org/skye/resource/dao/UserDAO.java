package org.skye.resource.dao;

import org.skye.domain.User;
import org.springframework.stereotype.Repository;

/**
 * DAO for the {@link org.skye.domain.User}
 */
@Repository
public class UserDAO extends AbstractPaginatingDAO<User> {

}
