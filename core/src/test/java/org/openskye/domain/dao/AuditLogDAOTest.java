package org.openskye.domain.dao;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.junit.Test;
import org.openskye.domain.*;

import javax.validation.ConstraintViolationException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
         Test the    AuditLogDAO
 */
public class AuditLogDAOTest extends AbstractDAOTestBase<AuditLog> {

    @Inject
    public AuditLogDAO auditLogDAO;
    @Override
    public AbstractPaginatingDAO getDAO() {
        return auditLogDAO;

    }

    @Override
    public AuditLog getNew() {
        AuditLog auditLog = new AuditLog();
        Domain domain=new Domain();
        domain.setName("Fishstick");
        Project project = new Project();
        project.setName("Starship 1");
        project.setDomain(domain);
        User user=new User();
        user.setName("Philip Dodds");
        user.setEmail("philip@fiveclouds.com");
        user.setDomain(domain);
        return auditLog;
    }

    @Override
    public void update(AuditLog instance) {
        instance.setAuditEntity("New AuditEntity");
        Domain domain=new Domain();
        User user=new User();
        user.setName("Philip Dodds");
        user.setEmail("philip@fiveclouds.com");
        user.setDomain(domain);
        instance.setUser(user);

    }

}


