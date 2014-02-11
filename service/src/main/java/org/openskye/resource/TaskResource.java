package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Injector;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.openskye.domain.Task;
import org.openskye.domain.TaskLog;
import org.openskye.domain.dao.*;
import org.openskye.exceptions.NotFoundException;
import org.openskye.task.TaskManager;
import org.openskye.task.step.*;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * The REST endpoint for {@link org.openskye.domain.Domain}
 */
@Api(value = "/api/1/tasks", description = "Manage tasks")
@Path("/api/1/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class TaskResource extends ProjectSpecificResource<Task> {

    private final ChannelDAO channelDAO;
    private TaskLogDAO taskLogDAO;
    private TaskDAO taskDAO;
    private TaskManager taskManager;
    private Injector injector;

    @Inject
    public TaskResource(TaskDAO dao, ChannelDAO channelDAO, TaskManager taskManager, TaskLogDAO taskLogDAO, Injector injector) {
        this.taskDAO = dao;
        this.channelDAO = channelDAO;
        this.taskLogDAO = taskLogDAO;
        this.taskManager = taskManager;
        this.injector = injector;
    }

    private Task createFromStep(TaskStep newStep) {
        injector.injectMembers(newStep);

        projectID = "*";

        // Some Tasks, like Reindex, may be set up to operate on all projects at once.
        // If this is the case, then the Project will be null, so the projectID that
        // Shiro looks at will default to *, for all projects.
        if(newStep.getProject() != null)
            projectID = newStep.getProject().getId();

        authorize("create");
        Task newInstance = super.create(newStep.toTask());
        taskManager.submit(newInstance);
        return newInstance;
    }

    @ApiOperation(value = "Create new discovery task", notes = "Create a new discovery task and return with its unique id", response = Task.class)
    @POST
    @Path("/discover")
    @Transactional
    @Timed
    public Task create(DiscoverTaskStep newStep) {
        return createFromStep(newStep);
    }

    @ApiOperation(value = "Create new replication task", notes = "Create a new replication task and return with its unique id", response = Task.class)
    @POST
    @Path("/replicate")
    @Transactional
    @Timed
    public Task create(ReplicateTaskStep newStep) {
        return createFromStep(newStep);
    }

    @ApiOperation(value = "Create new archive task", notes = "Create a new archive task and return with its unique id", response = Task.class)
    @POST
    @Path("/archive")
    @Transactional
    @Timed
    public Task create(ArchiveTaskStep newStep) {
        return createFromStep(newStep);
    }

    @ApiOperation(value = "Create new classify task", notes = "Create a new classify task and return with its unique id", response = Task.class)
    @POST
    @Path("/classify")
    @Transactional
    @Timed
    public Task classify(ClassifyTaskStep newStep) {
        return createFromStep(newStep);
    }

    @ApiOperation(value = "Create new cull task", notes = "Create a new cull task and return with its unique id", response = Task.class)
    @POST
    @Path("/cull")
    @Transactional
    @Timed
    public Task create(CullTaskStep newStep) {
        return createFromStep(newStep);
    }

    @ApiOperation(value = "Create new destroy task", notes = "Create a new destroy task and return with its unique id", response = Task.class)
    @POST
    @Path("/destroy")
    @Transactional
    @Timed
    public Task create(DestroyTaskStep newStep) {
        return createFromStep(newStep);
    }

    @ApiOperation(value = "Create new extract task", notes = "Create a new extract task and return with its unique id", response = Task.class)
    @POST
    @Path("/extract")
    @Transactional
    @Timed
    public Task create(ExtractTaskStep newStep) {
        return createFromStep(newStep);
    }

    @ApiOperation(value = "Create new verify task", notes = "Create a new verify task and return with its unique id", response = Task.class)
    @POST
    @Path("/verify")
    @Transactional
    @Timed
    public Task create(VerifyTaskStep newStep) {
        return createFromStep(newStep);
    }

    @ApiOperation(value = "Create new test task", notes = "Create a new test task and return with its unique id", response = Task.class)
    @POST
    @Path("/test")
    @Transactional
    @Timed
    public Task create(TestTaskStep newStep) {
        return createFromStep(newStep);
    }

    @ApiOperation(value = "Create new reindex task", notes = "Create a new reindex task and return with its unique id", response = Task.class)
    @POST
    @Path("/reindex")
    @Transactional
    @Timed
    public Task create(ReindexTaskStep newStep) {
        return createFromStep(newStep);
    }

    @ApiOperation(value = "Find task by id", notes = "Return a task by id", response = Task.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public Task get(@PathParam("id") String id) {
        authorize("get");
        if (taskDAO.get(id).isPresent()) {
            Task result = taskDAO.get(id).get();
            projectID = "*";

            if(result.getProject() != null)
                projectID = result.getProject().getId();

            return super.get(id);
        } else {
            throw new NotFoundException();
        }
    }

    @ApiOperation(value = "Find task logs for task by id", notes = "Return task logs for task by id", responseContainer = "List", response = TaskLog.class)
    @Path("/{id}/taskLogs")
    @GET
    @Transactional
    @Timed
    public PaginatedResult<TaskLog> getTaskLogs(@PathParam("id") String id) {
        if (taskDAO.get(id).isPresent()) {
            Task result = taskDAO.get(id).get();
            projectID = "*";

            if(result.getProject() != null)
                projectID = result.getProject().getId();

            authorize("get");
            return taskLogDAO.getLogsForTask(result);
        } else {
            throw new NotFoundException();
        }
    }

    @ApiOperation(value = "List all tasks", notes = "Returns all tasks in a paginated structure", responseContainer = "List", response = Task.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<Task> getAll() {
        PaginatedResult<Task> paginatedResult = super.getAll();
        List<Task> results = paginatedResult.getResults();
        for (Task t : results)
        {
            String taskProjectId = "*";

            if(t.getProject() != null)
                taskProjectId = t.getProject().getId();

            if (!isPermitted("list", taskProjectId))
            {
                results.remove(t);
            }
        }
        paginatedResult.setResults(results);
        return paginatedResult;
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
