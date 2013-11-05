package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.openskye.domain.Task;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.ChannelDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.TaskDAO;
import org.openskye.task.TaskManager;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.openskye.domain.Domain}
 */
@Api(value = "/api/1/tasks", description = "Manage tasks")
@Path("/api/1/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class TaskResource extends AbstractUpdatableDomainResource<Task> {

    private final ChannelDAO channelDAO;
    private TaskDAO taskDAO;
    private TaskManager taskManager;

    @Inject
    public TaskResource(TaskDAO dao, ChannelDAO channelDAO, TaskManager taskManager) {
        this.taskDAO = dao;
        this.channelDAO = channelDAO;
        this.taskManager = taskManager;
    }

    @ApiOperation(value = "Create new task", notes = "Create a new task and return with its unique id", response = Task.class)
    @POST
    @Transactional
    @Timed
    public Task create(Task newInstance) {
        authorize("create");
        super.create(newInstance);
        taskManager.submit(newInstance);
        return newInstance;
    }

    @ApiOperation(value = "Update task", notes = "Find a task by id and enter updated info. Returns updated task information", response = Task.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public Task update(@PathParam("id") String id, Task newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find task by id", notes = "Return a task by id", response = Task.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public Task get(@PathParam("id") String id) {
        return super.get(id);
    }

    @ApiOperation(value = "List all tasks", notes = "Returns all tasks in a paginated structure", responseContainer = "List", response = Task.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<Task> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete task", notes = "Deletes the task(found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
    }

    @Override
    protected AbstractPaginatingDAO<Task> getDAO() {
        return taskDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "task";
    }


}
