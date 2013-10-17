package org.openskye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.openskye.domain.TaskLog;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.TaskLogDAO;

import static org.mockito.Mockito.mock;

public class TaskLogResourceTest extends AbstractResourceTest<TaskLog> {

    public static final TaskLogDAO dao = mock(TaskLogDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TaskLogResource(dao))
            .build();
    private final TaskLog taskLog = new TaskLog();
    private PaginatedResult<TaskLog> expectedResult = new PaginatedResult<>();

    @Override
    public String getSingular() {
        return "taskLog";
    }

    @Override
    public String getPlural() {
        return "taskLogs";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public TaskLog getInstance() {
        return taskLog;
    }

    @Override
    public AbstractPaginatingDAO getDAO() {
        return dao;
    }

    @Override
    public PaginatedResult getExpectedResult() {
        return expectedResult;
    }
}