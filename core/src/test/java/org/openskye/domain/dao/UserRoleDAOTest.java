package org.openskye.domain.dao;

import org.openskye.domain.Domain;
import org.openskye.domain.Role;
import org.openskye.domain.User;
import org.openskye.domain.UserRole;

import javax.inject.Inject;

// Test the UserRoleDAO

public class UserRoleDAOTest extends AbstractDAOTestBase<UserRole> {


    @Inject
    public UserRoleDAO userRoleDAO;

    @Override
    public AbstractPaginatingDAO getDAO() {
        return userRoleDAO;
    }

    @Override
    public UserRole getNew() {
        Domain domain = new Domain();
        domain.setName("Fishstick");
        UserRole userRole = new UserRole();
        User user=new User();
        user.setName("Philip Dodds");
        user.setEmail("philip@fiveclouds.com");
        user.setDomain(domain);
        userRole.setUser(user);
        Role role=new Role();
        role.setName("Tester");
        userRole.setRole(role);
        return userRole;
    }

    @Override
    public void update(UserRole instance) {
        Domain domain=new Domain();
        domain.setName("Fishstick");
        User user=new User();
        user.setName("Philip Dodds");
        user.setEmail("philip@fiveclouds.com");
        user.setDomain(domain);
        instance.setUser(user);
        Role role=new Role();
        role.setName("Tester");
        instance.setRole(role);

    }


}
