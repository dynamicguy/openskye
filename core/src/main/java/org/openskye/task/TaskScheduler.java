package org.openskye.task;

import org.openskye.domain.TaskSchedule;

/**
 * TaskScheduler represents the way in which you request execution of tasks at a later time,
 * possibly according to a periodic schedule
 */
public interface TaskScheduler {

    /**
     * Start up the task scheduler
     */
    public void start();

    /**
     * Request submission of a {@link org.openskye.domain.Task} later, according to a {@link org.openskye.domain.TaskSchedule}
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
