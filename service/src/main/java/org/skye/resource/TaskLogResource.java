package org.skye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.skye.domain.TaskLog;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.PaginatedResult;
import org.skye.domain.dao.TaskLogDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/taskLogs", description = "Manage task logs")
@Path("/api/1/taskLogs")
@Produces(MediaType.APPLICATION_JSON)
/**
 * Manage task logs
 */
public class TaskLogResource extends AbstractUpdatableDomainResource<TaskLog> {

    private TaskLogDAO taskLogDAO;

    @Inject
    public TaskLogResource(TaskLogDAO dao) {
        this.taskLogDAO = dao;
    }

    @ApiOperation(value = "Create new task log", notes = "Create a new task log and return with its unique id", response = TaskLog.class)
    @POST
    @Transactional
    @Timed
    public TaskLog create(TaskLog newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update task log", notes = "Find the task log by id and enter new information. Returns updated task log", response = TaskLog.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public TaskLog update(@PathParam("id") String id, TaskLog newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find task log by id", notes = "Return a task log by id", response = TaskLog.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public TaskLog get(@PathParam("id") String id) {
        return super.get(id);
    }

    @ApiOperation(value = "List all task logs", notes = "Returns all task logs in a paginated structure", responseContainer = "List", response = TaskLog.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<TaskLog> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete task log", notes = "Deletes the task log (found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
    }

    @Override
    protected AbstractPaginatingDAO<TaskLog> getDAO() {
        return taskLogDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "taskLog";
    }


}
