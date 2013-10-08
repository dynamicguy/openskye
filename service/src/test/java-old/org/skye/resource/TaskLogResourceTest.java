package org.skye.resource;

import com.google.common.base.Optional;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.TaskLog;
import org.skye.domain.dao.PaginatedResult;
import org.skye.domain.dao.TaskLogDAO;

import javax.ws.rs.core.MediaType;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TaskLogResourceTest {
    private final TaskLog taskLog = new TaskLog();
    private final TaskLogDAO dao = mock(TaskLogDAO.class);
    private final Subject subject = mock(Subject.class);
    private PaginatedResult<TaskLog> expectedResult = new PaginatedResult<>();

    @Override
    protected void setUpResources() {
        when(dao.list()).thenReturn(expectedResult);
        when(dao.delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(Optional.of(taskLog));
        when(dao.create(taskLog)).thenReturn(taskLog);
        when(dao.update("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9", taskLog)).thenReturn(taskLog);
        TaskLogResource taskLogResource = new TaskLogResource();
        taskLogResource.taskLogDAO = dao;
        addResource(taskLogResource);
    }

    @Test
    public void testAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("taskLog:update")).thenReturn(true);
        assertThat(client().resource("/api/1/taskLogs/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(TaskLog.class, taskLog)).isEqualTo(taskLog);
    }

    @Test
    public void testUnAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("taskLog:update")).thenReturn(false);
        assertEquals(401, client().resource("/api/1/taskLogs/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, taskLog).getStatus());
    }

    @Test
    public void testAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("taskLog:create")).thenReturn(true);
        assertThat(client().resource("/api/1/taskLogs").type(MediaType.APPLICATION_JSON_TYPE).post(TaskLog.class, taskLog)).isEqualTo(taskLog);
    }

    @Test
    public void testUnAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("taskLog:create")).thenReturn(false);
        assertThat(client().resource("/api/1/taskLogs").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, taskLog).getStatus()).isEqualTo(401);
    }

    @Test
    public void testAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("taskLog:list")).thenReturn(true);
        assertThat(client().resource("/api/1/taskLogs").get(PaginatedResult.class)).isEqualTo(expectedResult);
    }

    @Test
    public void testUnAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("taskLog:list")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/taskLogs").get(PaginatedResult.class)).isEqualTo(expectedResult);
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }

    @Test
    public void testAuthorizedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("taskLog:delete")).thenReturn(true);
        ClientResponse response = client().resource("/api/1/taskLogs/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertThat(response.getStatus()).isEqualTo(200);

        verify(dao).delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("task:delete")).thenReturn(false);
        ClientResponse response = client().resource("/api/1/taskLogs/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    public void testAuthorizedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("taskLog:get")).thenReturn(true);
        assertThat(client().resource("/api/1/taskLogs/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(TaskLog.class))
                .isEqualTo(taskLog);
        verify(dao).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("taskLog:get")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/taskLogs/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(TaskLog.class));
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }
}