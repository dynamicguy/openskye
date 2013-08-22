package org.skye.resource;

import com.google.common.base.Optional;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.Project;
import org.skye.resource.dao.ProjectDAO;
import org.skye.util.PaginatedResult;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;
import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ProjectResourceTest extends ResourceTest {
    private final Project project = new Project();
    private final ProjectDAO dao = mock(ProjectDAO.class);
    private final Subject subject = mock(Subject.class);
    private PaginatedResult<Project> expectedResult = new PaginatedResult<>();

    @Override
    protected void setUpResources() {
        when(dao.list()).thenReturn(expectedResult);
        when(dao.delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(Optional.of(project));
        when(dao.persist(project)).thenReturn(project);
        ProjectResource projectResource = new ProjectResource();
        projectResource.projectDAO = dao;
        addResource(projectResource);
    }

    @Test
    public void testAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("project:update")).thenReturn(true);
        assertThat(client().resource("/api/1/projects/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(Project.class, project)).isEqualTo(project);
    }

    @Test
    public void testUnAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("project:update")).thenReturn(false);
        assertEquals(401, client().resource("/api/1/projects/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, project).getStatus());
    }

    @Test
    public void testAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("project:create")).thenReturn(true);
        assertThat(client().resource("/api/1/projects").type(MediaType.APPLICATION_JSON_TYPE).post(Project.class, project)).isEqualTo(project);
    }

    @Test
    public void testUnAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("project:create")).thenReturn(false);
        assertEquals(401, client().resource("/api/1/projects").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, project).getStatus());
    }

    @Test
    public void testAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("project:list")).thenReturn(true);
        assertThat(client().resource("/api/1/projects").get(PaginatedResult.class)).isEqualTo(expectedResult);
    }

    @Test
    public void testUnAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("project:list")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/projects").get(PaginatedResult.class)).isEqualTo(expectedResult);
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }

    @Test
    public void testAuthorizedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("project:delete")).thenReturn(true);
        ClientResponse response = client().resource("/api/1/projects/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertThat(response.getStatus()).isEqualTo(200);

        verify(dao).delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("project:delete")).thenReturn(false);
        ClientResponse response = client().resource("/api/1/projects/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertThat(response.getStatus()).isEqualTo(401);
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

    @Test
    public void testAuthorizedGetChannels() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("project:get")).thenReturn(true);
        assertThat(client().resource("/api/1/projects/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9/channels").get(Project.class))
                .isEqualTo(project);
        verify(dao).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGetChannels() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("project:get")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/projects/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9/channels").get(Project.class));
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }
}