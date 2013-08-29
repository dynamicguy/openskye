package org.skye.util;

import com.google.inject.persist.PersistService;
import lombok.extern.slf4j.Slf4j;
import org.skye.domain.Domain;
import org.skye.domain.User;
import org.skye.resource.dao.DomainDAO;
import org.skye.resource.dao.UserDAO;

import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * This will create the infobelt domain and the default accounts if they don't exist
 */
@Slf4j
public class CreateDefaultAccount {

    @Inject
    private UserDAO userDAO;
    @Inject
    private DomainDAO domainDAO;

    @Inject
    private EntityManager entityManager;

    @Inject CreateDefaultAccount(PersistService service) {

        service.start();

        // At this point JPA is started and ready.

    }

    @Inject
    public void init() {
        log.info("Checking for default admin account");

        if (!userDAO.findByEmail("admin@skye.org").isPresent()) {
            entityManager.getTransaction().begin();
            log.info("Creating default admin account");
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
            entityManager.getTransaction().commit();
        }
    }
}
