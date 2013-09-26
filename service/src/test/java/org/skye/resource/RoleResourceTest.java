package org.skye.resource;

import com.google.common.base.Optional;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.Role;
import org.skye.domain.dao.PaginatedResult;
import org.skye.domain.dao.RoleDAO;

import javax.ws.rs.core.MediaType;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class RoleResourceTest extends ResourceTest {
    private final Role role = new Role();
    private final RoleDAO dao = mock(RoleDAO.class);
    private final Subject subject = mock(Subject.class);
    private PaginatedResult<Role> expectedResult = new PaginatedResult<>();

    @Override
    protected void setUpResources() {
        when(dao.list()).thenReturn(expectedResult);
        when(dao.delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(Optional.of(role));
        when(dao.create(role)).thenReturn(role);
        when(dao.update("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9", role)).thenReturn(role);
        RoleResource roleResource = new RoleResource();
        roleResource.roleDAO = dao;
        addResource(roleResource);
    }

    @Test
    public void testAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("role:update")).thenReturn(true);
        assertThat(client().resource("/api/1/roles/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(Role.class, role)).isEqualTo(role);
    }

    @Test
    public void testUnAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("role:update")).thenReturn(false);
        assertEquals(401, client().resource("/api/1/roles/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, role).getStatus());
    }

    @Test
    public void testAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("role:create")).thenReturn(true);
        assertThat(client().resource("/api/1/roles").type(MediaType.APPLICATION_JSON_TYPE).post(Role.class, role)).isEqualTo(role);
    }

    @Test
    public void testUnAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("role:create")).thenReturn(false);
        assertEquals(401, client().resource("/api/1/roles").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, role).getStatus());
    }

    @Test
    public void testAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("role:list")).thenReturn(true);
        assertThat(client().resource("/api/1/roles").get(PaginatedResult.class)).isEqualTo(expectedResult);
    }

    @Test
    public void testUnAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("role:list")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/roles").get(PaginatedResult.class)).isEqualTo(expectedResult);
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }

    @Test
    public void testAuthorizedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("role:delete")).thenReturn(true);
        ClientResponse response = client().resource("/api/1/roles/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertThat(response.getStatus()).isEqualTo(200);

        verify(dao).delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("role:delete")).thenReturn(false);
        ClientResponse response = client().resource("/api/1/roles/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    public void testAuthorizedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("role:get")).thenReturn(true);
        assertThat(client().resource("/api/1/roles/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(Role.class))
                .isEqualTo(role);
        verify(dao).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("role:get")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/roles/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(Role.class));
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }

    @Test
    public void testAuthorizedGetPermissions() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("role:get")).thenReturn(true);
        assertThat(client().resource("/api/1/roles/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9/permissions").get(Role.class))
                .isEqualTo(role);
        verify(dao).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGetPermissions() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("role:get")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/roles/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9/permissions").get(Role.class));
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }
}