package org.skye.hadoop.task;

import org.skye.domain.Task;
import org.skye.task.TaskManager;

/**
 * An implementation of the {@link TaskManager} that operates using Hadoop
 * to manage the running of the jobs.
 * <p/>
 * Note: this requires that the OMR is running using HBase so it is accessible
 * from the jobs
 */
public class HadoopTaskManager implements TaskManager {
    @Override
    public void submit(Task task) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
