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

}
