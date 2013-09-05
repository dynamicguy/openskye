package org.skye.resource;

import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.skye.domain.AttributeDefinition;
import org.skye.domain.Task;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.TaskDAO;
import org.skye.task.TaskManager;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/tasks", description = "Manage tasks")
@Path("/api/1/tasks")
@Produces(MediaType.APPLICATION_JSON)
/**
 * Manage domains
 */
public class TaskResource extends AbstractUpdatableDomainResource<Task> {

    @Inject
    protected TaskDAO taskDAO;

    @Inject
    protected TaskManager taskManager;

    @ApiOperation(value = "Create new", notes = "Create a new instance and return with id", response = Task.class)
    @POST
    @Transactional
    @Timed
    public Task create(Task newInstance){
        Task task = super.create(newInstance);
        taskManager.submit(task);
        return task;
    }

    @ApiOperation(value = "Update instance", notes = "Update the instance", response = Task.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public Task update(@PathParam("id") String id, Task newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find by id", notes = "Return an instance by id", response = Task.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public Task get(@PathParam("id") String id) {
        return super.get(id);
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
