package org.openskye.task.simple;

import org.openskye.domain.TaskSchedule;
import org.openskye.task.TaskScheduler;

/**
 * Stub scheduler for testing only
 */
public class InMemoryTaskScheduler implements TaskScheduler {
    @Override
    public void start() {
        //stub
    }

    @Override
    public void schedule(TaskSchedule taskSchedule) {
        //stub
    }

    @Override
    public void unschedule(String taskScheduleId) {
        //stub
    }
}
