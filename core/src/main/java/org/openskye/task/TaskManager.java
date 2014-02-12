package org.openskye.task;

import org.openskye.domain.Task;

/**
 * Task Manager represents the way in which can you create a new task
 * and then interact with it
 */
public interface TaskManager {

    /**
     * Start up the task manager
     */
    void start();

    /**
     * Request execution of a {@link Task} when resources are available
     *
     * @param task task to submit
     */
    void submit(Task task);

    /**
     * Record an event in the task log
     *
     * @param task the task which triggered the event
     * @param message message to be entered in the log
     * @param e exception thrown, if applicable
     */
    void toLog(Task task, String message, Exception e);
    void toLog(Task task, String message);

}
