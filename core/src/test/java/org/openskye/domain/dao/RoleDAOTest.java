package org.openskye.domain.dao;

import com.google.inject.Inject;
import org.openskye.domain.Role;

/**
 Test the RoleDAO */
public class RoleDAOTest  extends AbstractDAOTestBase<Role>{

    @Inject
    public RoleDAO roleDAO;


    @Override
    public AbstractPaginatingDAO<Role> getDAO() {
        return roleDAO;
    }

    @Override
    public Role getNew() {
       Role role=new Role();
        role.setName("Tester");
                  return  role;
    }

    @Override
    public void update(Role instance) {
        instance.setName("Tester");
    }
}
