package org.openskye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.openskye.domain.Permission;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.PermissionDAO;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import static org.mockito.Mockito.mock;

public class PermissionResourceTest extends AbstractResourceTest<Permission> {

    public static final PermissionDAO dao = mock(PermissionDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new PermissionResource(dao))
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
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
    public PaginatedResult getExpectedResult() {
        return expectedResult;
    }
}