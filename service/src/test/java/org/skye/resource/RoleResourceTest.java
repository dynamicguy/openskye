package org.skye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.skye.domain.Role;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.PaginatedResult;
import org.skye.domain.dao.RoleDAO;

import static org.mockito.Mockito.mock;

public class RoleResourceTest extends AbstractResourceTest<Role> {

    public static final RoleDAO dao = mock(RoleDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new RoleResource(dao))
            .build();
    private final Role role = new Role();
    private PaginatedResult<Role> expectedResult = new PaginatedResult<>();

    @Override
    public String getSingular() {
        return "role";
    }

    @Override
    public String getPlural() {
        return "roles";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public Role getInstance() {
        return role;
    }

    @Override
    public AbstractPaginatingDAO getDAO() {
        return dao;
    }

    @Override
    public PaginatedResult getExpectedResult() {
        return expectedResult;
    }
}