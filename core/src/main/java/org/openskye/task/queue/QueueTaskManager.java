package org.openskye.task.queue;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.openskye.core.SkyeException;
import org.openskye.domain.Task;
import org.openskye.domain.TaskLog;
import org.openskye.domain.TaskStatus;
import org.openskye.domain.dao.TaskDAO;
import org.openskye.domain.dao.TaskLogDAO;
import org.openskye.domain.dao.TaskScheduleDAO;
import org.openskye.task.TaskManager;

/**
 * An implementation of the {@link org.openskye.task.TaskManager} that stores queued jobs in the database
 * it expects instances of WorkerTask to pick up the tasks and run them.
 */
@Slf4j
public class QueueTaskManager implements TaskManager {

    @Inject
    TaskScheduleDAO taskScheduleDAO;
    @Inject
    TaskLogDAO taskLogDAO;
    @Inject
    TaskDAO taskDAO;
    @Inject
    Injector injector;

    @Override
    public void start() {
    }

    @Override
    public void submit(Task task) {
        enqueue(task.getId());
    }

    protected Task getTask(String taskId) {
        Optional<Task> optTask = taskDAO.get(taskId);
        if (optTask == null || !optTask.isPresent()) {
            throw new SkyeException("Task " + taskId + " not found");
        } else {
            return optTask.get();
        }
    }

    protected void toLog(Task task, String message, Exception e) {
        TaskLog taskLog = new TaskLog();
        taskLog.setTaskId(task.getId());
        taskLog.setStatus(task.getStatus());
        taskLog.setMessage(message);
        taskLog.setException(e);
        taskLogDAO.create(taskLog);
    }

    protected void toLog(Task task, String message) {
        toLog(task, message, null);
    }

    /**
     * A new task is queued {@link Task} for execution
     *
     * @param taskId id of task to enqueue
     */
    protected void enqueue(String taskId) {
        Task task = getTask(taskId);
        if (task.getWorkerName() == null) {
            throw new SkyeException("Must set worker name before submitting task " + taskId);
        }
        switch (task.getStatus()) {
            case CREATED:
            case FAILED:
            case ABORTED:
                task.setStatus(TaskStatus.QUEUED);
                task.setQueued(LocalDateTime.now());
                taskDAO.update(taskId, task);
                toLog(task, "Task Queued");
                break;
            default:
                throw new SkyeException("Cannot enqueue " + task.getStatus() + " task " + taskId);
        }
    }

    /**
     * Record the acceptance of a queued {@link Task} for execution
     *
     * @param taskId     id of task to accept
     * @param workerName name identifying the thread that accepts the task
     */
    protected void accept(String taskId, String workerName) {
        Task task = getTask(taskId);
        if (!task.getWorkerName().equals(workerName)) {
            throw new SkyeException("Worker " + workerName + " cannot accept task meant for " + task.getWorkerName());
        }
        switch (task.getStatus()) {
            case QUEUED:
                task.setStatus(TaskStatus.STARTED);
                task.setWorkerName(workerName);
                task.setStarted(LocalDateTime.now());
                taskDAO.update(taskId, task);
                toLog(task, "Task Accepted");
                break;
            default:
                throw new SkyeException("Cannot accept " + task.getStatus() + " task " + taskId);
        }
    }

    /**
     * Record the end of a previously running {@link Task}
     *
     * @param taskId    id of task that ended
     * @param status    final status of the task
     * @param exception exception that caused the task to end, if any
     */
    protected void end(String taskId, TaskStatus status, Exception exception) {
        Task task = getTask(taskId);
        switch (task.getStatus()) {
            case STARTED:
                task.setStatus(status);
                task.setEnded(LocalDateTime.now());
                taskDAO.update(taskId, task);
                toLog(task, "Task Ended with " + status + " status", exception);
                break;
            default:
                throw new SkyeException("Cannot end " + task.getStatus() + " task " + taskId);
        }
    }

}
