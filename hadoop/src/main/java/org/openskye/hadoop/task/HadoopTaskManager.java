package org.openskye.hadoop.task;

import org.openskye.domain.Task;
import org.openskye.domain.TaskSchedule;
import org.openskye.task.TaskManager;

/**
 * An implementation of the {@link TaskManager} that operates using Hadoop
 * to manage the running of the jobs.
 * <p/>
 * Note: this requires that the OMR is running using HBase so it is accessible
 * from the jobs
 */
public class HadoopTaskManager implements TaskManager {
    @Override
    public void init() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void submit(Task task) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void schedule(TaskSchedule taskSchedule) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unschedule(String taskScheduleId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
