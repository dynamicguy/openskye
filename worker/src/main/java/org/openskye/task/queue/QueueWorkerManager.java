package org.openskye.task.queue;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import org.openskye.config.WorkerConfiguration;
import org.openskye.core.SkyeException;
import org.openskye.domain.Node;
import org.openskye.domain.Task;
import org.openskye.domain.TaskStatus;
import org.openskye.domain.dao.NodeDAO;
import org.openskye.domain.dao.TaskDAO;
import org.openskye.task.step.TaskStep;

import javax.persistence.EntityManager;
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
    @Inject
    WorkerConfiguration workerConfig;
    // monitor schedules the QueueWorkerManager's own thread to manage tasks
    // workers is a pool of worker threads that runs tasks
    // activeTasks is a thread safe map from task id to worker
    ScheduledExecutorService monitor;
    ExecutorService workers;
    @Inject
    TaskDAO taskDAO;
    @Inject
    NodeDAO nodeDAO;
    @Inject
    Injector injector;
    @Inject
    private Provider<EntityManager> emf;
    // keep a map from task id's to futures for submitted task steps
    private Map<String, Future<TaskStatus>> futures;

    public QueueWorkerManager() {
        // Concurrent map so that REST status requests are in sync with monitor
        futures = new ConcurrentHashMap<>();
    }

    public String[] getActiveTaskIds() {
        return (String[]) (futures.keySet().toArray());
    }

    @Override
    public void start() {

        // We need to ensure that the node is properly set-up
        if (workerConfig.getNodeId() == null || workerConfig.getNodeId().isEmpty())
            throw new SkyeException("You have not set-up the nodeId in your configuration,  create a node on the server and then configure this worker with its node id.");

        Optional<Node> node = nodeDAO.get(workerConfig.getNodeId());
        if (!node.isPresent())
            throw new SkyeException("Node id " + workerConfig.getNodeId() + " is not registered,  worker WILL NOT function!");

        monitor = Executors.newSingleThreadScheduledExecutor();
        workers = Executors.newFixedThreadPool(workerConfig.getThreadCount());
        monitor.scheduleAtFixedRate(this, 0, workerConfig.getPollPeriodSec(), TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        log.debug(workerConfig.getNodeId() + ": monitor wakes up ..");

        // Look for tasks that have ended
        Set<String> taskIds = futures.keySet();
        for (String taskId : taskIds) {
            Future<TaskStatus> future = futures.get(taskId);
            TaskStatus status;
            Exception exception = null;
            if (future == null) {
                log.error("Unable to track worker thread for task " + taskId);
                status = TaskStatus.ABORTED;
            } else if (future.isCancelled()) {
                log.info("Task has been canceled " + taskId);
                status = TaskStatus.ABORTED;
                futures.remove(taskId);
            } else if (future.isDone()) {
                try {
                    status = future.get();
                    log.info("Task completed " + taskId);
                } catch (Exception e) {
                    status = TaskStatus.FAILED;
                    exception = e;
                    log.error("Task failed " + taskId, e);
                }
                futures.remove(taskId);
            } else {
                // Task is still running
                status = TaskStatus.STARTED;
            }

            if (status != TaskStatus.STARTED) {
                // The task has ended, so update its status
                try {
                    emf.get().getTransaction().begin();
                    end(taskId, status, exception);
                    emf.get().getTransaction().commit();
                } catch (Exception e) {
                    log.error("Error while recording end of task " + taskId, e);
                    log.debug(workerConfig.getNodeId() + ": end task " + taskId);
                }
            }
        }

        // Submit tasks to the worker thread pool, oldest first,
        // until the max worker thread count is met
        int tries = 0;
        int maxThreads = workerConfig.getThreadCount();
        boolean queuedTasks = true;
        while (tries < maxThreads && futures.size() < maxThreads && queuedTasks) {
            try {
                Optional<Task> task = taskDAO.findOldestQueued(workerConfig.getNodeId());
                if (task == null || !task.isPresent()) {
                    queuedTasks = false;
                } else {
                    String taskId = task.get().getId();
                    TaskStep step = task.get().getStep();

                    // Inject and rehydrate everything
                    injector.injectMembers(step);
                    step.rehydrate();

                    emf.get().getTransaction().begin();
                    accept(taskId, workerConfig.getNodeId());
                    emf.get().getTransaction().commit();

                    futures.put(taskId, workers.submit(step));
                    log.debug(workerConfig.getNodeId() + ": begin task " + taskId);
                }
            } catch (Exception e) {
                log.error("Exception while accepting task", e);
            }
            tries++;
        }
    }
}
