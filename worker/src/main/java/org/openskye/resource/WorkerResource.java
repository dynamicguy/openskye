package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.config.WorkerConfiguration;
import org.openskye.task.TaskManager;
import org.openskye.task.queue.QueueWorkerManager;
import org.openskye.util.WorkerInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for accessing the worker's status information
 */
@Api(value = "/api/1/worker", description = "Request worker status")
@Path("/api/1/worker")
@Produces(MediaType.APPLICATION_JSON)
public class WorkerResource {

    @Inject
    private TaskManager taskManager;
    @Inject
    private WorkerConfiguration workerConfig;

    @ApiOperation(value = "Return the current status of the worker", notes = "Task and domain info is omitted by default for brevity", response = WorkerInfo.class)
    @GET
    @Transactional
    @Timed
    public WorkerInfo getWorkerInfo() {
        WorkerInfo info = new WorkerInfo();
        info.setConfiguration(workerConfig);
        QueueWorkerManager queueWorkerManager = (QueueWorkerManager) taskManager;
        info.setTaskIds(queueWorkerManager.getActiveTaskIds());
        return info;
    }

}
