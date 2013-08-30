package org.skye.util;

import com.google.inject.persist.PersistService;
import lombok.extern.slf4j.Slf4j;
import org.skye.domain.*;
import org.skye.resource.dao.DomainDAO;
import org.skye.resource.dao.PermissionDAO;
import org.skye.resource.dao.RoleDAO;
import org.skye.resource.dao.UserDAO;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

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

            List<Permission> perms = new ArrayList<>();
            Permission p = new Permission();
            p.setPermission("*");
            permissionDAO.persist(p);

            perms.add(p);
            Role role = new Role();
            role.setName("administrator");
            role.setPermissions(perms);
            roleDAO.persist(role);

            User adminUser = new User();
            adminUser.setName("Skye Admin");
            adminUser.setDomain(domain);
            adminUser.setEmail("admin@skye.org");
            adminUser.setPassword("changeme");
            adminUser.encryptPassword();
            UserRole uRole = new UserRole();
            uRole.setRole(role);
            uRole.setUser(adminUser);

            List<UserRole> userRoleList = new ArrayList<>();
            userRoleList.add(uRole);
            adminUser.setUserRoles(userRoleList);
            userDAO.persist(adminUser);
            entityManager.getTransaction().commit();
        }
    }
}
