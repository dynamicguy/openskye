package org.skye.resource;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.Role;
import org.skye.domain.Task;
import org.skye.resource.dao.RoleDAO;
import org.skye.resource.dao.TaskDAO;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TaskResourceTest extends ResourceTest {
    private final Task task = new Task();
    private final TaskDAO dao = mock(TaskDAO.class);
    private final Subject subject = mock(Subject.class);

    @Override
    protected void setUpResources() {
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(task);
        TaskResource taskResource = new TaskResource();
        taskResource.taskDAO = dao;
        addResource(taskResource);
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