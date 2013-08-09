package org.skye.resource;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.Project;
import org.skye.domain.User;
import org.skye.resource.dao.ProjectDAO;
import org.skye.resource.dao.UserDAO;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ProjectResourceTest extends ResourceTest {
    private final Project project = new Project();
    private final ProjectDAO dao = mock(ProjectDAO.class);
    private final Subject subject = mock(Subject.class);

    @Override
    protected void setUpResources() {
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(project);
        ProjectResource projectResource = new ProjectResource();
        projectResource.projectDAO = dao;
        addResource(projectResource);
    }

    @Test
    public void testAuthorizedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("project:get")).thenReturn(true);
        assertThat(client().resource("/api/1/projects/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(Project.class))
                .isEqualTo(project);
        verify(dao).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("project:get")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/projects/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(Project.class));
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }
}