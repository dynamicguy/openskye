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

            Permission all = new Permission();
            all.setPermission("*");
            permissionDAO.create(all);

            Role adminRole = new Role();
            adminRole.setName("administrator");

            RolePermission adminRP = new RolePermission();
            adminRP.setRole(adminRole);
            adminRP.setPermission(all);

            adminRole.setRolePermissions(ImmutableList.of(adminRP));
            roleDAO.create(adminRole);

            User adminUser = new User();
            adminUser.setName("Skye Admin");
            adminUser.setDomain(domain);
            adminUser.setEmail("admin@openskye.org");
            adminUser.setPassword("changeme");
            adminUser.setApiKey("123");
            adminUser.encryptPassword();

            UserRole uRole = new UserRole();
            uRole.setRole(adminRole);
            uRole.setUser(adminUser);

            adminUser.setUserRoles(ImmutableList.of(uRole));
            userDAO.create(adminUser);

            userDAO.getEntityManagerProvider().get().getTransaction().commit();
        }

        CreateDefaultAccount.log.info("Checking for default readonly account");

        if (!userDAO.findByEmail("reader@openskye.org").isPresent()) {
            userDAO.getEntityManagerProvider().get().getTransaction().begin();
            CreateDefaultAccount.log.info("Creating default readonly account");

            Domain domain = domainDAO.findByName("Skye").get();

            if (domain == null) {
                domain = createDomain();
            }

            Permission read = new Permission();
            read.setPermission("*:get");
            permissionDAO.create(read);

            Permission list = new Permission();
            list.setPermission("*:list");
            permissionDAO.create(list);

            Role readonlyRole = new Role();
            readonlyRole.setName("readonly");

            RolePermission rp = new RolePermission();
            rp.setRole(readonlyRole);
            rp.setPermission(read);

            RolePermission rp2 = new RolePermission();
            rp2.setRole(readonlyRole);
            rp2.setPermission(list);

            readonlyRole.setRolePermissions(ImmutableList.of(rp, rp2));
            roleDAO.create(readonlyRole);

            User readonlyUser = new User();
            readonlyUser.setName("Skye Read-only User");
            readonlyUser.setDomain(domain);
            readonlyUser.setEmail("reader@openskye.org");
            readonlyUser.setPassword("changeme");
            readonlyUser.setApiKey("456");
            readonlyUser.encryptPassword();

            UserRole uRole = new UserRole();
            uRole.setRole(readonlyRole);
            uRole.setUser(readonlyUser);

            readonlyUser.setUserRoles(ImmutableList.of(uRole));
            userDAO.create(readonlyUser);

            userDAO.getEntityManagerProvider().get().getTransaction().commit();
        }
    }

    Domain createDomain() {
        Domain domain = new Domain();
        domain.setName("Skye");
        domainDAO.create(domain);
        return domain;
    }
}
