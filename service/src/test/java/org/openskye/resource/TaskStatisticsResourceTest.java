package org.openskye.resource;


import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.openskye.domain.TaskStatistics;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.ChannelDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.TaskStatisticsDAO;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;
import org.openskye.task.TaskManager;

import static org.mockito.Mockito.mock;

public class TaskStatisticsResourceTest extends AbstractResourceTest<TaskStatistics> {

    public static final TaskStatisticsDAO dao=mock(TaskStatisticsDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TaskStatisticsResource(dao))
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
            .build();

    private  TaskStatistics taskStatistics = new TaskStatistics();
    private PaginatedResult<TaskStatistics> expectedResult=new PaginatedResult<>();

    @Override
    public String getSingular() {
        return "taskStatistics";
    }

    @Override
    public String getPlural() {
        return "taskStatistics";
    }
    @Override
    public ResourceTestRule getResources() {
        return resources;
    }
    @Override
    public TaskStatistics getInstance() {
        return taskStatistics;
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
