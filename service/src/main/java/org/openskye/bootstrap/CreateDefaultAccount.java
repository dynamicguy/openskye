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

    /**
     * Starts the persistence service
     *
     * @param service the Java Persistence Service
     */
    @Inject
    CreateDefaultAccount(PersistService service) {

        service.start();

        // At this point JPA is started and ready.

    }

    /**
     * <P>Checks for the default administrator user and the default read-only user in the skye database, creates them if
     * they don't exist.
     * <p/>
     * Once complete, the following users exist in the Skye database: </P> <p> <B>Default Admin:</B> a default account
     * with full administrator privileges for all objects - <BR /> Name: Skye Admin <BR /> Domain: Skye <BR /> Role:
     * administrator <br /> Email: admin@openskye.org <br /> Password: changeme <br /> API Key: 123 <br /> </p>
     * <p/>
     * <p> <B>Default Read-only User:</B> a default account with read-only privileges for all objects - <br /> Name:
     * Skye Read-Only User <br /> Domain: Skye <br /> Role: readonly <br /> Email: reader@openskye.org <br /> Password:
     * changeme <br /> API Key: 456 <br /> </p>
     */
    @Inject
    public void init() {
        log.info("Checking for default admin account");

        if (!userDAO.findByEmail("admin@openskye.org").isPresent()) {
            userDAO.getEntityManagerProvider().get().getTransaction().begin();
            log.info("Creating default admin account");
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

        log.info("Checking for default readonly account");

        if (!userDAO.findByEmail("reader@openskye.org").isPresent()) {
            userDAO.getEntityManagerProvider().get().getTransaction().begin();
            log.info("Creating default readonly account");

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
        userDAO.getEntityManagerProvider().get().close();
    }

    /**
     * Creates the default Skye domain
     *
     * @return the created domain
     */
    Domain createDomain() {
        Domain domain = new Domain();
        domain.setName("Skye");
        domainDAO.create(domain);
        return domain;
    }
}
