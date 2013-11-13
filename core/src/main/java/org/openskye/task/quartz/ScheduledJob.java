package org.openskye.task.quartz;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.openskye.core.SkyeException;
import org.openskye.domain.Task;
import org.openskye.domain.TaskSchedule;
import org.openskye.domain.dao.TaskDAO;
import org.openskye.domain.dao.TaskScheduleDAO;
import org.openskye.task.TaskManager;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * A Task scheduled to run via Quartz
 */
public class ScheduledJob implements org.quartz.Job {
    @Inject
    TaskManager taskManager;
    @Inject
    TaskDAO taskDAO;
    @Inject
    TaskScheduleDAO taskScheduleDAO;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String taskScheduleId = jobExecutionContext.getMergedJobDataMap().getString("taskScheduleId");
        Optional<TaskSchedule> taskSchedule = taskScheduleDAO.get(taskScheduleId);
        if ( taskSchedule == null || ! taskSchedule.isPresent() ) {
            throw new SkyeException("TaskSchedule "+taskScheduleId+" not found");
        } else {
            Task task = taskDAO.create(taskSchedule.get());
            taskManager.submit(task);
        }
    }
}
