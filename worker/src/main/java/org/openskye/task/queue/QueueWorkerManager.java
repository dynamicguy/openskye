package org.openskye.task.queue;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import org.openskye.bootstrap.CreateNode;
import org.openskye.config.SkyeWorkerConfiguration;
import org.openskye.core.SkyeException;
import org.openskye.domain.Node;
import org.openskye.domain.Task;
import org.openskye.domain.TaskStatus;
import org.openskye.domain.dao.NodeDAO;
import org.openskye.domain.dao.TaskDAO;
import org.openskye.node.NodeManager;
import org.openskye.task.step.TaskStep;

import javax.persistence.EntityManager;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
    SkyeWorkerConfiguration workerConfig;
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
    CreateNode createNode;
    @Inject
    private Provider<EntityManager> emf;
    // keep a map from task id's to futures for submitted task steps
    private Map<String, Future<TaskStatus>> futures;
    private Node currentNode;

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
        if (workerConfig.getWorkerConfiguration().getHostname() == null || workerConfig.getWorkerConfiguration().getHostname().isEmpty())
            try {
                workerConfig.getWorkerConfiguration().setHostname(InetAddress.getLocalHost().getHostName());
            } catch (UnknownHostException e) {
                throw new SkyeException("Unable to get the hostname for this worker,  therefore we can not identify which node we are on", e);
            }

        emf.get().getTransaction().begin();
        Optional<Node> node = nodeDAO.findByHostname(workerConfig.getWorkerConfiguration().getHostname());
        if (!node.isPresent()) {
            log.info("Creating node for hostname " + workerConfig.getWorkerConfiguration().getHostname());
            currentNode = createNode.createNode(workerConfig.getWorkerConfiguration().getHostname());
            emf.get().getTransaction().commit();
        } else {
            currentNode = node.get();
            log.info("Got node " + node);
            emf.get().getTransaction().rollback();
        }

        // Set which node we are on
        NodeManager.setNode(currentNode);


        monitor = Executors.newSingleThreadScheduledExecutor();
        workers = Executors.newFixedThreadPool(workerConfig.getWorkerConfiguration().getThreadCount());
        monitor.scheduleAtFixedRate(this, 0, workerConfig.getWorkerConfiguration().getPollPeriodSec(), TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        log.debug(workerConfig.getWorkerConfiguration().getHostname() + ": monitor wakes up ..");

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
                    log.debug(workerConfig.getWorkerConfiguration().getHostname() + ": end task " + taskId);
                }
            }
        }

        // Submit tasks to the worker thread pool, oldest first,
        // until the max worker thread count is met
        int tries = 0;
        int maxThreads = workerConfig.getWorkerConfiguration().getThreadCount();
        boolean queuedTasks = true;
        while (tries < maxThreads && futures.size() < maxThreads && queuedTasks) {
            try {
                Optional<Task> task = taskDAO.findOldestQueued(currentNode);
                if (task == null || !task.isPresent()) {
                    queuedTasks = false;
                } else {
                    String taskId = task.get().getId();
                    TaskStep step = task.get().getStep();

                    // Inject and rehydrate everything
                    injector.injectMembers(step);
                    step.rehydrate();

                    emf.get().getTransaction().begin();
                    accept(taskId, workerConfig.getWorkerConfiguration().getHostname());
                    emf.get().getTransaction().commit();

                    futures.put(taskId, workers.submit(step));
                    log.debug(workerConfig.getWorkerConfiguration().getHostname() + ": begin task " + taskId);
                }
            } catch (Exception e) {
                log.error("Exception while accepting task", e);
            }
            tries++;
        }
    }
}
