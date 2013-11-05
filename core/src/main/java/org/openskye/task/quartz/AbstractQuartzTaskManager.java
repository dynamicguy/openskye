package org.openskye.task.quartz;

import org.openskye.core.SkyeException;
import org.openskye.domain.TaskSchedule;
import org.openskye.task.TaskManager;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.TriggerKey.triggerKey;

/**
 * Task scheduling abstract class based on Quartz
 */
public abstract class AbstractQuartzTaskManager implements TaskManager {

    private Scheduler quartzScheduler;
    private String quartzJobGroup = "Orion";

    @Override
    public void init() {
        // Start the Quartz scheduler
        try {
            quartzScheduler = new StdSchedulerFactory().getScheduler();
            quartzScheduler.start();
        } catch (SchedulerException se) {
            throw new SkyeException("Unable to start Quartz scheduler",se);
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
