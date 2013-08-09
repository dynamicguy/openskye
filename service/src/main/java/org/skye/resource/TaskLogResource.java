package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import org.skye.domain.Domain;
import org.skye.domain.TaskLog;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.DomainDAO;
import org.skye.resource.dao.TaskLogDAO;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

    @Override
    protected AbstractPaginatingDAO<TaskLog> getDAO() {
        return taskLogDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "taskLog";
    }


}
