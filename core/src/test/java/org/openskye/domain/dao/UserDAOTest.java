package org.openskye.domain.dao;

import com.google.common.base.Optional;
import org.junit.Test;
import org.openskye.domain.Domain;
import org.openskye.domain.User;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Testing the {@link org.openskye.domain.dao.UserDAO}
 */
public class UserDAOTest extends AbstractDAOTestBase<User> {

    @Inject
    public UserDAO userDAO;

    @Override
    public AbstractPaginatingDAO getDAO() {
        return userDAO;
    }

    @Override
    public User getNew() {
        Domain domain = new Domain();
        domain.setName("Fishstick");
        User user = new User();
        user.setName("Philip Dodds");
        user.setEmail("philip@fiveclouds.com");
        user.setDomain(domain);

        return user;
    }

    @Override
    public void update(User instance) {
        instance.setName("Philly D");
    }

}
