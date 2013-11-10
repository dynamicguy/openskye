package org.openskye.task.quartz;

import org.openskye.core.SkyeException;
import org.openskye.domain.TaskSchedule;
import org.openskye.domain.dao.TaskScheduleDAO;
import org.openskye.task.TaskManager;
import org.openskye.task.TaskScheduler;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.inject.Inject;

import static org.quartz.TriggerKey.triggerKey;

/**
 * Task scheduling abstract class based on Quartz
 */
public class QuartzTaskScheduler implements TaskScheduler {

    private Scheduler quartzScheduler;
    private String quartzJobGroup = "Orion";

    @Inject
    TaskScheduleDAO taskScheduleDAO;

    @Override
    public void start() {
        // Start the Quartz scheduler
        try {
            quartzScheduler = new StdSchedulerFactory().getScheduler();
            quartzScheduler.start();
        } catch (SchedulerException se) {
            throw new SkyeException("Unable to run Quartz scheduler",se);
        }

        // Retrieve all TaskSchedule records from the
        // database and load them into the scheduler.
        for ( TaskSchedule taskSchedule : taskScheduleDAO.list().getResults() ) {
            schedule(taskSchedule);
        }
    }

    @Override
    public void schedule(TaskSchedule taskSchedule) {
        String jobName = taskSchedule.getId();
        JobDetail job = JobBuilder.newJob(ScheduledJob.class)
                .withIdentity(jobName,quartzJobGroup)
                .usingJobData("taskScheduleId", taskSchedule.getId())
                .build();
        try {
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobName, quartzJobGroup)
                    .withSchedule(CronScheduleBuilder.cronSchedule(taskSchedule.getCronExpression()))
                    .build();
            quartzScheduler.scheduleJob(job, trigger);
        } catch( SchedulerException se ) {
            throw new SkyeException("Unable to schedule task",se);
        }
    }

    @Override
    public void unschedule(String taskScheduleId) {
        try {
            quartzScheduler.unscheduleJob(triggerKey(taskScheduleId, quartzJobGroup));
        } catch (SchedulerException se) {
            throw new SkyeException("Unable to unschedule task",se);
        }
    }
}
