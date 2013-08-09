package org.skye.resource;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.Permission;
import org.skye.domain.User;
import org.skye.resource.dao.PermissionDAO;
import org.skye.resource.dao.UserDAO;
import org.skye.util.PaginatedResult;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PermissionResourceTest extends ResourceTest {
    private final Permission permission = new Permission();
    private final PermissionDAO dao = mock(PermissionDAO.class);
    private final Subject subject = mock(Subject.class);
    private PaginatedResult<Permission> expectedResult = new PaginatedResult<>();

    @Override
    protected void setUpResources() {
        when(dao.list()).thenReturn(expectedResult);
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(permission);
        PermissionResource permissionResource = new PermissionResource();
        permissionResource.permissionDAO = dao;
        addResource(permissionResource);
    }

    @Test
    public void testAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("permission:list")).thenReturn(true);
        assertThat(client().resource("/api/1/permissions").get(PaginatedResult.class)).isEqualTo(expectedResult);
    }

    @Test
    public void testUnAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("permission:list")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/permissions").get(PaginatedResult.class)).isEqualTo(expectedResult);
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }

    @Test
    public void testAuthorizedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("permission:get")).thenReturn(true);
        assertThat(client().resource("/api/1/permissions/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(Permission.class))
                .isEqualTo(permission);
        verify(dao).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("permission:get")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/permissions/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(Permission.class));
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }
}