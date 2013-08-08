package org.skye.resource;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.User;
import org.skye.resource.dao.UserDAO;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserResourceTest extends ResourceTest {
    private final User user = new User();
    private final UserDAO dao = mock(UserDAO.class);
    private final Subject subject = mock(Subject.class);

    @Override
    protected void setUpResources() {
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(user);
        UserResource userResource = new UserResource();
        userResource.userDAO = dao;
        addResource(userResource);
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
}