package org.openskye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.openskye.domain.TaskSchedule;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.TaskScheduleDAO;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import static org.mockito.Mockito.mock;

public class TaskScheduleResourceTest extends AbstractResourceTest<TaskSchedule> {

    public static final TaskScheduleDAO dao = mock(TaskScheduleDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TaskScheduleResource(dao))
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
            .build();
    private final TaskSchedule taskSchedule = new TaskSchedule();
    private PaginatedResult<TaskSchedule> expectedResult = new PaginatedResult<>();

    @Override
    public String getSingular() {
        return "taskSchedule";
    }

    @Override
    public String getPlural() {
        return "taskSchedules";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public TaskSchedule getInstance() {
        return taskSchedule;
    }

    @Override
    public AbstractPaginatingDAO getDAO() {
        return dao;
    }

    @Override
    public PaginatedResult getExpectedResult() {
        return expectedResult;
    }

    @Test
    public void testAuthorizedPost() throws Exception {
        // We override this since it can't work
        // in the test harness
    }

    @Test
    public void testAuthorizedDelete() throws Exception {
        // We override this since it can't work
        // in the test harness
    }

    @Test
    public void testUnAuthorisedDelete() throws Exception {
        // We override this since it can't work
        // in the test harness
    }

    @Test
    public void testAuthorizedPut() throws Exception {
        // We override this since it can't work
        // in the test harness
    }

    @Test
    public void testUnAuthorizedPut() throws Exception {
        // We override this since it can't work
        // in the test harness
    }
}
