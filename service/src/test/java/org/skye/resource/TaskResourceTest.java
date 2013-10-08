package org.skye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.skye.domain.Task;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.PaginatedResult;
import org.skye.domain.dao.TaskDAO;

import static org.mockito.Mockito.mock;

public class TaskResourceTest extends AbstractResourceTest<Task> {
    private static final TaskDAO dao = mock(TaskDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TaskResource(dao))
            .build();
    private final Task task = new Task();
    private PaginatedResult<Task> expectedResult = new PaginatedResult<>();

    @Override
    public String getSingular() {
        return "task";
    }

    @Override
    public String getPlural() {
        return "tasks";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public Task getInstance() {
        return task;
    }

    @Override
    public AbstractPaginatingDAO getDAO() {
        return dao;
    }

    @Override
    public Object getExpectedResult() {
        return expectedResult;
    }
}