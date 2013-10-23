package org.openskye.domain.dao;

import org.openskye.domain.Domain;
import org.openskye.domain.User;

import javax.inject.Inject;

/**
 * Testing the {@link org.openskye.domain.dao.UserDAO}
 */
public class UserDAOTest extends AbstractDAOTestBase<User> {

    @Inject
    public UserDAO domainDAO;

    @Override
    public AbstractPaginatingDAO getDAO() {
        return domainDAO;
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
