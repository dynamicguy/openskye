package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.domain.TaskSchedule;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.TaskScheduleDAO;
import org.openskye.task.TaskManager;
import org.openskye.task.TaskScheduler;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.openskye.domain.TaskSchedule}
 */
@Api(value = "/api/1/taskSchedules", description = "Manage task schedules")
@Path("/api/1/taskSchedules")
public class TaskScheduleResource extends AbstractUpdatableDomainResource<TaskSchedule> {

    private TaskScheduleDAO taskScheduleDAO;
    @Inject
    private TaskManager taskManager;
    @Inject
    private TaskScheduler taskScheduler;

    @Inject
    public TaskScheduleResource(TaskScheduleDAO dao) {
        this.taskScheduleDAO = dao;
    }

    @Override
    protected AbstractPaginatingDAO<TaskSchedule> getDAO() {
        return taskScheduleDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "taskSchedule";
    }

    @ApiOperation(value = "Create new task schedule", notes = "Create a new task schedule and return with its unique id", response = TaskSchedule.class)
    @POST
    @Transactional
    @Timed
    @Override
    public TaskSchedule create(TaskSchedule newInstance) {
        TaskSchedule taskSchedule = super.create(newInstance);
        taskScheduler.schedule(taskSchedule);
        return taskSchedule;
    }

    @ApiOperation(value = "Update instance", notes = "Find the task schedule to update by id and enter the new information. Returns updated task schedule information", response = TaskSchedule.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public TaskSchedule update(@PathParam("id") String id, TaskSchedule newInstance) {
        TaskSchedule taskSchedule =  super.update(id, newInstance);
        taskScheduler.unschedule(taskSchedule.getId());
        taskScheduler.schedule(taskSchedule);
        return taskSchedule;
    }

    @ApiOperation(value = "Find task schedule by id", notes = "Return a task schedule by their unique id", response = TaskSchedule.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public TaskSchedule get(@PathParam("id") String id) {
        return super.get(id);
    }

    @ApiOperation(value = "List all task schedules", notes = "Returns all task schedules in a paginated structure", responseContainer = "List", response = TaskSchedule.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<TaskSchedule> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete task schedule", notes = "Delete the task schedule (found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        taskScheduler.unschedule(id);
        return super.delete(id);
    }
}
