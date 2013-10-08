package org.skye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.skye.domain.Permission;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.PaginatedResult;
import org.skye.domain.dao.PermissionDAO;

import static org.mockito.Mockito.mock;

public class PermissionResourceTest extends AbstractResourceTest<Permission> {
    private static final PermissionDAO dao = mock(PermissionDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new PermissionResource(dao))
            .build();
    private final Permission permission = new Permission();
    private PaginatedResult<Permission> expectedResult = new PaginatedResult<>();

    @Override
    public String getSingular() {
        return "permission";
    }

    @Override
    public String getPlural() {
        return "permissions";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public Permission getInstance() {
        return permission;
    }

    @Override
    public AbstractPaginatingDAO getDAO() {
        return dao;
    }

    @Override
    public Object getExpectedResult() {
        return expectedResult;
    }
}