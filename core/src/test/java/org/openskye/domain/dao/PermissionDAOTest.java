package org.openskye.domain.dao;

import com.google.inject.Inject;
import org.openskye.domain.Domain;
import org.openskye.domain.Permission;
import org.openskye.domain.User;

/**
 Test the  PermissionDAO
 */
    public class PermissionDAOTest extends AbstractDAOTestBase<Permission>{

        @Inject
    public PermissionDAO permissionDAO;


    @Override
    public AbstractPaginatingDAO getDAO() {
        return permissionDAO;
    }

    @Override
    public Permission getNew() {
        Permission permission=new Permission();
        permission.setPermission("Test permission");

        return permission;
    }

    @Override
    public void update(Permission instance) {
        instance.setPermission("Test permission");
    }
}

