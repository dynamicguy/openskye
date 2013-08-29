package org.skye.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;
import org.skye.core.SkyeException;
import org.skye.domain.Domain;
import org.skye.domain.User;
import org.skye.resource.dao.DomainDAO;
import org.skye.resource.dao.UserDAO;

import javax.inject.Inject;

/**
 * This will create the infobelt domain and the default accounts if they don't exist
 */
@Slf4j
public class CreateDefaultAccount {

    @Inject
    private SessionFactory sessionFactory;
    @Inject
    private UserDAO userDAO;
    @Inject
    private DomainDAO domainDAO;

    @Inject
    public void init() {
        log.info("Checking for default admin account");
        final Session session = sessionFactory.openSession();
        try {
            ManagedSessionContext.bind(session);
            try {
                if (!userDAO.findByEmail("admin@skye.org").isPresent()) {
                    log.info("Creating default admin account");
                    Transaction trans = session.beginTransaction();
                    Domain domain = new Domain();
                    domain.setName("Skye");
                    domainDAO.persist(domain);

                    User adminUser = new User();
                    adminUser.setName("Skye Admin");
                    adminUser.setDomain(domain);
                    adminUser.setEmail("admin@skye.org");
                    adminUser.setPassword("changeme");
                    adminUser.encryptPassword();
                    userDAO.persist(adminUser);
                    trans.commit();
                }
            } catch (AuthenticationException e) {
                throw new SkyeException("Unable to authenticate user", e);
            }
        } finally {
            session.close();
            ManagedSessionContext.unbind(sessionFactory);
        }
    }
}
