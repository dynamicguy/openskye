package org.openskye.bootstrap;

import com.google.common.collect.ImmutableList;
import com.google.inject.persist.PersistService;
import lombok.extern.slf4j.Slf4j;
import org.openskye.domain.*;
import org.openskye.domain.dao.DomainDAO;
import org.openskye.domain.dao.PermissionDAO;
import org.openskye.domain.dao.RoleDAO;
import org.openskye.domain.dao.UserDAO;

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
    private RoleDAO roleDAO;
    @Inject
    private PermissionDAO permissionDAO;

    @Inject
    CreateDefaultAccount(PersistService service) {

        service.start();

        // At this point JPA is started and ready.

    }

    @Inject
    public void init() {
        CreateDefaultAccount.log.info("Checking for default admin account");

        if (!userDAO.findByEmail("admin@openskye.org").isPresent()) {
            userDAO.getEntityManagerProvider().get().getTransaction().begin();
            CreateDefaultAccount.log.info("Creating default admin account");
            Domain domain = new Domain();
            domain.setName("Skye");
            domainDAO.create(domain);

            Permission p = new Permission();
            p.setPermission("*");
            permissionDAO.create(p);

            Role role = new Role();
            role.setName("administrator");

            RolePermission rp = new RolePermission();
            rp.setRole(role);
            rp.setPermission(p);
            role.setRolePermissions(ImmutableList.of(rp));
            roleDAO.create(role);
            User adminUser = new User();
            adminUser.setName("Skye Admin");
            adminUser.setDomain(domain);
            adminUser.setEmail("admin@openskye.org");
            adminUser.setPassword("changeme");
            adminUser.setApiKey("123");
            adminUser.encryptPassword();
            UserRole uRole = new UserRole();
            uRole.setRole(role);
            uRole.setUser(adminUser);

            adminUser.setUserRoles(ImmutableList.of(uRole));
            userDAO.create(adminUser);

            userDAO.getEntityManagerProvider().get().getTransaction().commit();

        }
    }
}
