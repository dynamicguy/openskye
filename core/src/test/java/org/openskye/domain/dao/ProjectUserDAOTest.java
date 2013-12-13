package org.openskye.domain.dao;

import com.google.inject.Inject;
import org.openskye.domain.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User: atcmostafavi
 * Date: 12/12/13
 * Time: 1:01 PM
 * Project: platform
 */
public class ProjectUserDAOTest extends AbstractDAOTestBase<ProjectUser> {

    @Inject
    private ProjectUserDAO projectUserDAO;

    @Override
    public AbstractPaginatingDAO<ProjectUser> getDAO() {
        return projectUserDAO;
    }

    @Override
    public ProjectUser getNew() {
        Domain domain = new Domain();
        domain.setName("Fishstick");
        User user=new User();
        user.setId("1234");
        user.setName("Philip Dodds");
        user.setEmail("philip@fiveclouds.com");
        user.setDomain(domain);

        Project project = new Project();
        project.setId("5678");
        project.setName("Test Project");

        Role role=new Role();
        role.setName("Tester");
        Permission perm = new Permission();
        perm.setPermission("*:*:"+project.getId());
        RolePermission rp = new RolePermission();
        rp.setRole(role);
        rp.setPermission(perm);
        List<RolePermission> rolePermissions = new ArrayList<RolePermission>();
        rolePermissions.add(rp);
        role.setRolePermissions(rolePermissions);

        ProjectUser projectUser = new ProjectUser();
        projectUser.setUser(user);
        projectUser.setProject(project);
        projectUser.addRole(role);
        return projectUser;


    }

    @Override
    public void update(ProjectUser instance) {
        ProjectUser current = getNew();
        instance.setProject(current.getProject());
        instance.setUser(current.getUser());
        instance.setProjectUserRoles(current.getProjectUserRoles());
    }
}
