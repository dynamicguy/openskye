package org.openskye.task;

import org.openskye.domain.Domain;
import org.openskye.domain.Task;
import org.openskye.domain.TaskSchedule;

/**
 * Task Manager represents the way in which can you create a new task
 * and then interact with it
 */
public interface TaskManager {

    /**
     * Initialize the task manager
     */
    void init();

    /**
     * Request execution of a {@link Task} when resources are available
     *
     * @param task task to submit
     */
    void submit(Task task);

    /**
     * Request submission of a {@link Task} later, according to a {@link TaskSchedule}
     *
     * @param taskSchedule schedule to create
     */
    public void schedule(TaskSchedule taskSchedule);

    /**
     * Unschedule means to cancel previous scheduling of a {@link TaskSchedule}
     *
     * @param taskScheduleId schedule to delete
     */
    public void unschedule(String taskScheduleId);

}
