package org.skye.task;

import org.skye.domain.Task;

/**
 * Task Manager represents the way in which can you create a new task
 * and then interact with it
 */
public interface TaskManager {

    /**
     * Allows you to submit a {@link Task}
     *
     * @param task task to submit
     */
    void submit(Task task);
}
