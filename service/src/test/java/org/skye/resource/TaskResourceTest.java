package org.skye.resource;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.Role;
import org.skye.domain.Task;
import org.skye.resource.dao.RoleDAO;
import org.skye.resource.dao.TaskDAO;
import org.skye.util.PaginatedResult;

import javax.ws.rs.core.MediaType;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TaskResourceTest extends ResourceTest {
    private final Task task = new Task();
    private final TaskDAO dao = mock(TaskDAO.class);
    private final Subject subject = mock(Subject.class);
    private PaginatedResult<Task> expectedResult = new PaginatedResult<>();

    @Override
    protected void setUpResources() {
        when(dao.list()).thenReturn(expectedResult);
        when(dao.delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(task);
        when(dao.persist(task)).thenReturn(task);
        TaskResource taskResource = new TaskResource();
        taskResource.taskDAO = dao;
        addResource(taskResource);
    }

    @Test
    public void testAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("task:update")).thenReturn(true);
        assertThat(client().resource("/api/1/tasks/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(Task.class, task)).isEqualTo(task);
    }

    @Test
    public void testUnAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("task:update")).thenReturn(false);
        assertEquals(401,client().resource("/api/1/tasks/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, task).getStatus());
    }

    @Test
    public void testAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("task:create")).thenReturn(true);
        assertThat(client().resource("/api/1/tasks").type(MediaType.APPLICATION_JSON_TYPE).post(Task.class, task)).isEqualTo(task);
    }

    @Test
    public void testUnAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("task:create")).thenReturn(false);
        assertEquals(401,client().resource("/api/1/tasks").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, task).getStatus());
    }

    @Test
    public void testAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("task:list")).thenReturn(true);
        assertThat(client().resource("/api/1/tasks").get(PaginatedResult.class)).isEqualTo(expectedResult);
    }

    @Test
    public void testUnAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("task:list")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/tasks").get(PaginatedResult.class)).isEqualTo(expectedResult);
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }

    @Test
    public void testAuthorizedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("task:delete")).thenReturn(true);
        ClientResponse response = client().resource("/api/1/tasks/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertEquals(200,response.getStatus());

        verify(dao).delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }
    @Test
    public void testUnAuthorisedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("task:delete")).thenReturn(false);
        ClientResponse response = client().resource("/api/1/tasks/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertEquals(401,response.getStatus());
    }

    @Test
    public void testAuthorizedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("task:get")).thenReturn(true);
        assertThat(client().resource("/api/1/tasks/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(Task.class))
                .isEqualTo(task);
        verify(dao).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("tasks:get")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/tasks/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(Task.class));
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }
}