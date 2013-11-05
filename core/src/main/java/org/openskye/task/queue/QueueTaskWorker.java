package org.openskye.task.queue;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openskye.domain.Task;
import org.openskye.domain.TaskStatus;
import org.openskye.domain.dao.TaskDAO;
import org.openskye.task.TaskLogger;
import org.openskye.task.step.AbstractTaskStep;
import org.openskye.task.step.TaskStep;

import javax.persistence.*;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Looks for queued jobs and runs them, oldest first
 */
@Slf4j
public class QueueTaskWorker implements Runnable {

    static String workerName = null;
    static ScheduledThreadPoolExecutor executor = null;
    static int threadCount = 0;
    @Getter
    static CopyOnWriteArrayList<String> taskIdList = null;

    public static void init(String workerName,int threadCount,long period,TimeUnit unit) {
        QueueTaskWorker.workerName = workerName;
        QueueTaskWorker.threadCount = threadCount;
        QueueTaskWorker.taskIdList = new CopyOnWriteArrayList<String>();
        executor = new ScheduledThreadPoolExecutor(threadCount);
        executor.scheduleAtFixedRate(new QueueTaskWorker(),0,period,unit);
    }

    public static void shutdown() {
        if ( executor != null ) {
            executor.shutdown();
            executor = null;
        }
    }

    @Inject
    TaskDAO taskDAO;

    @Inject
    Injector injector;

    private void update(Task task,TaskStatus status) {
        // Attempt an exclusive lock while updating the task
        taskDAO.lock(task, LockModeType.PESSIMISTIC_WRITE);
        TaskLogger taskLogger = new TaskLogger(task);
        task.setStatus(status);
        taskLogger.info("Worker "+workerName+" "+status+" task");
        switch( status ) {
            case STARTING:
                task.setWorkerName(workerName);
                break;
            case RUNNING:
                task.setStarted(new Date());
                break;
            case COMPLETED:
            case CANCELED:
            case FAILED:
                task.setEnded(new Date());
                break;
        }
        taskDAO.update(task.getId(),task);

        // Keep a shared lock on the task even after updating, so that no other worker can grab it
        taskDAO.lock(task, LockModeType.PESSIMISTIC_READ);
    }

    public void release(Task task) {
        // Release the lock on the task
        taskDAO.lock(task, LockModeType.NONE);
    }

    @Override
    public void run() {
        Optional<Task> nextTask = taskDAO.findOldestQueued();
        if ( nextTask == null || ! nextTask.isPresent() ) {
            return;
        }

        Task task = nextTask.get();
        TaskLogger taskLogger = new TaskLogger(task);
        try {
            update(task,TaskStatus.STARTING);
            taskIdList.add(task.getId());
            injector.injectMembers(task.getStep());
            update(task,TaskStatus.RUNNING);
            TaskStatus finalStatus;
            try {
                task.getStep().start();
                finalStatus = TaskStatus.COMPLETED;
            } catch(Exception se) {
                finalStatus = TaskStatus.FAILED;
                taskLogger.error("Worker "+workerName+" caught exception",se);
            }
            update(task, finalStatus);
            taskIdList.remove(task.getId());
            release(task);
        } catch(PessimisticLockException|LockTimeoutException pe) {
            // Another worker has probably grabbed the task
            taskLogger.info("Worker "+workerName+" found task already locked");
        } catch(Exception e) {
            taskLogger.error("Worker "+workerName+" Error",e);
        }

        // Make sure any outstanding lock is released
        if ( task != null ) {
            release(task);
        }
    }

}
