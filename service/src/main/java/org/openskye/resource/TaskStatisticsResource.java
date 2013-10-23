package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.domain.TaskStatistics;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.TaskStatisticsDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.openskye.domain.Domain}
 */
@Api(value = "/api/1/taskStatistics", description = "Manage task statistics")
@Path("/api/1/taskStatistics")
@Produces(MediaType.APPLICATION_JSON)
/**
 * Manage domains
 */
public class TaskStatisticsResource extends AbstractUpdatableDomainResource<TaskStatistics> {

    @Inject
    protected TaskStatisticsDAO taskStatisticsDAO;

    @ApiOperation(value = "Create new task statistics", notes = "Create a new task statistics and return with its unique id", response = TaskStatistics.class)
    @POST
    @Transactional
    @Timed
    public TaskStatistics create(TaskStatistics newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update task statistics", notes = "Find a task statistics by id and enter updated info. Returns updated task statistics information", response = TaskStatistics.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public TaskStatistics update(@PathParam("id") String id, TaskStatistics newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find task statistics by id", notes = "Return a task statistics by id", response = TaskStatistics.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public TaskStatistics get(@PathParam("id") String id) {
        return super.get(id);
    }

    @ApiOperation(value = "List all task statistics", notes = "Returns all task statistics in a paginated structure", responseContainer = "List", response = TaskStatistics.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<TaskStatistics> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete task statistics", notes = "Deletes the task statistics(found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
    }

    @Override
    protected AbstractPaginatingDAO<TaskStatistics> getDAO() {
        return taskStatisticsDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "taskStatistics";
    }


}
