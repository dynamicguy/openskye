package org.skye.resource;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.TaskLog;
import org.skye.resource.dao.TaskLogDAO;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TaskLogResourceTest extends ResourceTest {
    private final TaskLog taskLog = new TaskLog();
    private final TaskLogDAO dao = mock(TaskLogDAO.class);
    private final Subject subject = mock(Subject.class);

    @Override
    protected void setUpResources() {
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(taskLog);
        TaskLogResource taskLogResource = new TaskLogResource();
        taskLogResource.taskLogDAO = dao;
        addResource(taskLogResource);
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