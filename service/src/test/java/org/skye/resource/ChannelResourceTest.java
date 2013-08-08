package org.skye.resource;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.Role;
import org.skye.resource.dao.RoleDAO;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ChannelResourceTest extends ResourceTest {
    private final Role role = new Role();
    private final RoleDAO dao = mock(RoleDAO.class);
    private final Subject subject = mock(Subject.class);

    @Override
    protected void setUpResources() {
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(role);
        RoleResource roleResource = new RoleResource();
        roleResource.roleDAO = dao;
        addResource(roleResource);
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
}