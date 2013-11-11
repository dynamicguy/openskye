package org.openskye.task.queue;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openskye.config.WorkerConfiguration;
import org.openskye.domain.Task;
import org.openskye.domain.TaskStatus;
import org.openskye.domain.dao.TaskDAO;
import org.openskye.task.step.TaskStep;

import javax.persistence.EntityTransaction;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * A thread that periodically checks that all worker threads are still alive and working.
 * If a thread is found to have suddenly stopped, update the database and active tasks list
 * to show that the job
 */
@Slf4j
public class QueueWorkerManager extends QueueTaskManager implements Runnable {
    // Worker configuration controls operation of the worker threads
    @Getter
    @Setter
    WorkerConfiguration workerConfiguration = null;

    // monitor schedules the QueueWorkerManager's own thread to manage tasks
    // workers is a pool of worker threads that runs tasks
    // activeTasks is a thread safe map from task id to worker
    ScheduledExecutorService monitor;
    ExecutorService workers;

    // keep a map from task id's to futures for submitted task steps
    private Map<String,Future<TaskStatus>> futures;

    public QueueWorkerManager() {
        // Default settings for the worker configuration
        workerConfiguration = new WorkerConfiguration();
        workerConfiguration.setName("Orion Worker");
        workerConfiguration.setThreadCount(3);
        workerConfiguration.setPollPeriodSec(20);
        // Concurrent map so that REST status requests are in sync with monitor
        futures = new ConcurrentHashMap<String,Future<TaskStatus>>();
    }

    public String[] getActiveTaskIds() {
        return (String[])(futures.keySet().toArray());
    }

    @Override
    public void start() {
        monitor = Executors.newSingleThreadScheduledExecutor();
        workers = Executors.newFixedThreadPool(workerConfiguration.getThreadCount());
        monitor.scheduleAtFixedRate(this, 0, workerConfiguration.getPollPeriodSec(), TimeUnit.SECONDS);
    }

    @Inject
    TaskDAO taskDAO;

    @Override
    public void run() {

        // Look for tasks that have ended
        Set<String> taskIds = futures.keySet();
        for( String taskId : taskIds ) {
            Future<TaskStatus> future = futures.get(taskId);
            TaskStatus status;
            Exception exception = null;
            if ( future == null ) {
                log.error("Unable to track worker thread for task "+taskId);
                status = TaskStatus.ABORTED;
            } else if ( future.isCancelled() ) {
                log.info("Task has been canceled "+taskId);
                status = TaskStatus.ABORTED;
                futures.remove(taskId);
            } else if ( future.isDone() ) {
                try {
                    status = future.get();
                    log.info("Task completed "+taskId);
                } catch(Exception e) {
                    status = TaskStatus.FAILED;
                    exception = e;
                    log.error("Task failed "+taskId,e);
                }
                futures.remove(taskId);
            } else {
                // Task is still running
                status = TaskStatus.STARTED;
            }

            if ( status != TaskStatus.STARTED ) {
                // The task has ended, so update its status
                try {
                    EntityTransaction tx = taskDAO.beginTransaction();
                    end(taskId,status,exception);
                    tx.commit();
                } catch( Exception e ) {
                    log.error("Error while recording end of task "+taskId,e);
                }
            }
        }

        // Submit tasks to the worker thread pool, oldest first,
        // until the max worker thread count is met
        int tries = 0;
        int maxThreads = workerConfiguration.getThreadCount();
        boolean queuedTasks = true;
        while ( tries < maxThreads && futures.size() < maxThreads && queuedTasks ) {
            try {
                Optional<Task> task = taskDAO.findOldestQueued(workerConfiguration.getName());
                if ( task == null || ! task.isPresent() ) {
                    queuedTasks = false;
                } else {
                    String taskId = task.get().getId();
                    TaskStep step = task.get().getStep();
                    EntityTransaction tx = taskDAO.beginTransaction();
                    accept(taskId,workerConfiguration.getName());
                    tx.commit();
                    futures.put(taskId, workers.submit(step));
                }
            } catch (Exception e) {
                log.error("Exception while accepting task",e);
            }
            tries++;
        }
    }
}
