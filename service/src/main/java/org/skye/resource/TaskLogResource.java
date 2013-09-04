package org.skye.resource;

import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.skye.domain.AttributeDefinition;
import org.skye.domain.TaskLog;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.TaskLogDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/taskLogs", description = "Manage task logs")
@Path("/api/1/taskLogs")
@Produces(MediaType.APPLICATION_JSON)
/**
 * Manage domains
 */
public class TaskLogResource extends AbstractUpdatableDomainResource<TaskLog> {

    @Inject
    protected TaskLogDAO taskLogDAO;

    @ApiOperation(value = "Create new", notes = "Create a new instance and return with id", response = TaskLog.class)
    @POST
    @Transactional
    @Timed
    public TaskLog create(TaskLog newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update instance", notes = "Update the instance", response = TaskLog.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public TaskLog update(@PathParam("id") String id, TaskLog newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find by id", notes = "Return an instance by id", response = TaskLog.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public TaskLog get(@PathParam("id") String id) {
        return super.get(id);
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
