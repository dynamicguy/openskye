package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import org.skye.domain.Task;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.TaskDAO;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

    @Override
    protected AbstractPaginatingDAO<Task> getDAO() {
        return taskDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "task";
    }


}
