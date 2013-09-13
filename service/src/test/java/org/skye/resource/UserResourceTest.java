package org.skye.resource;

import com.google.common.base.Optional;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.User;
import org.skye.domain.dao.PaginatedResult;
import org.skye.domain.dao.UserDAO;

import javax.ws.rs.core.MediaType;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserResourceTest extends ResourceTest {
    private final User user = new User();
    private final UserDAO dao = mock(UserDAO.class);
    private final Subject subject = mock(Subject.class);
    private PaginatedResult<User> expectedResult = new PaginatedResult<>();

    @Override
    protected void setUpResources() {
        when(dao.list()).thenReturn(expectedResult);
        when(dao.delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(Optional.of(user));
        when(dao.persist(user)).thenReturn(user);
        UserResource userResource = new UserResource();
        userResource.userDAO = dao;
        addResource(userResource);
    }

    @Test
    public void testAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("user:update")).thenReturn(true);
        assertThat(client().resource("/api/1/users/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(User.class, user)).isEqualTo(user);
    }

    @Test
    public void testUnAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("user:update")).thenReturn(false);
        assertEquals(401, client().resource("/api/1/users/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, user).getStatus());
    }

    @Test
    public void testAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("user:create")).thenReturn(true);
        assertThat(client().resource("/api/1/users").type(MediaType.APPLICATION_JSON_TYPE).post(User.class, user)).isEqualTo(user);
    }

    @Test
    public void testUnAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("user:create")).thenReturn(false);
        assertEquals(401, client().resource("/api/1/users").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, user).getStatus());
    }

    @Test
    public void testAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("user:list")).thenReturn(true);
        assertThat(client().resource("/api/1/users").get(PaginatedResult.class)).isEqualTo(expectedResult);
    }

    @Test
    public void testUnAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("user:list")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/users").get(PaginatedResult.class)).isEqualTo(expectedResult);
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }

    @Test
    public void testAuthorizedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("user:delete")).thenReturn(true);
        ClientResponse response = client().resource("/api/1/users/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertThat(response.getStatus()).isEqualTo(200);

        verify(dao).delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("user:delete")).thenReturn(false);
        ClientResponse response = client().resource("/api/1/users/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    public void testAuthorizedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("user:get")).thenReturn(true);
        assertThat(client().resource("/api/1/users/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(User.class))
                .isEqualTo(user);
        verify(dao).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("user:get")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/users/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(User.class));
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }

    @Test
    public void testAuthorizedGetUserRoles() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("user:get")).thenReturn(true);
        assertThat(client().resource("/api/1/users/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9/roles").get(User.class)).isEqualTo(user);
        verify(dao).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGetUserRoles() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("user:get")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/users/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9/roles").get(User.class));
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }
}