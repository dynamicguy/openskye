package org.openskye.task.queue;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openskye.domain.Task;
import org.openskye.domain.TaskSchedule;
import org.openskye.domain.TaskStatus;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.TaskDAO;
import org.openskye.domain.dao.TaskScheduleDAO;
import org.openskye.task.TaskLogger;
import org.openskye.task.quartz.AbstractQuartzTaskManager;

import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.quartz.TriggerKey.triggerKey;

/**
 * An implementation of the {@link org.openskye.task.TaskManager} that stores queued jobs in the database
 * it expects instances of QueueTaskWorker to pick up the tasks and run them.  The workers may be on this
 * host or on another host connected to the same database.
 */
@Slf4j
public class QueueTaskManager extends AbstractQuartzTaskManager implements Runnable {

    @Inject
    TaskDAO taskDAO;
    @Inject
    TaskScheduleDAO taskScheduleDAO;

    @Getter
    @Setter
    private long monitorPeriod = 2;
    @Getter
    @Setter
    private TimeUnit monitorUnit = TimeUnit.MINUTES;

    @Override
    public void init() {
        super.init();

        // Retrieve all TaskSchedule records from the
        // database and load them into the scheduler.
        for ( TaskSchedule taskSchedule : taskScheduleDAO.list().getResults() ) {
            schedule(taskSchedule);
        }

        // Start a monitoring thread to clean up after any aborted workers
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(this,0,monitorPeriod,monitorUnit);
    }

    @Override
    public void submit(Task task) {
        TaskLogger taskLogger = new TaskLogger(task);
        task.setQueued(new Date());
        task.setStatus(TaskStatus.QUEUED);
        task.setWorkerName(null);
        task.setStarted(null);
        taskDAO.update(task.getId(),task);
        taskLogger.info("Task Submitted to queue");
    }

    private void fail(Task task) {
        TaskLogger taskLogger = new TaskLogger(task);
        task.setEnded(new Date());
        task.setStatus(TaskStatus.FAILED);
        taskDAO.update(task.getId(),task);
        taskLogger.error("Task Failed due to unknown cause");
    }

    @Override
    public void run() {
        // Check that any allegedly STARTING or RUNNING tasks are actually locked by a live worker
        // If not, set STARTING tasks back to QUEUED status and reset the date queued, and
        // set RUNNING tasks to FAILED since the worker seems to have stopped mid-stream.
        PaginatedResult<Task> liveTasks = taskDAO.findLiveTasks();
        for ( Task task : liveTasks.getResults() ) {
            try {
                taskDAO.lock(task, LockModeType.PESSIMISTIC_WRITE);
                switch( task.getStatus() ) {
                    case STARTING:
                        submit(task);
                        break;
                    case RUNNING:
                        fail(task);
                        break;
                }
                taskDAO.lock(task, LockModeType.NONE);
            } catch( PersistenceException pe ) {
                // If it's locked, leave it alone since a live worker is probably running the task
            } catch( Exception e ) {
                log.error("Queue Task Manager Initialization Error",e);
                taskDAO.lock(task, LockModeType.NONE);
            }
        }
    }

}
