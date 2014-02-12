package org.openskye.task.simple;

import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.SkyeException;
import org.openskye.domain.Task;
import org.openskye.domain.TaskStatus;
import org.openskye.task.TaskManager;
import org.openskye.task.step.TaskStep;

import javax.inject.Inject;

/**
 * An implementation of the {@link TaskManager} that operates in-memory
 * <p/>
 * NOTE: This is not thread-safe and is really in place to allow for testing of the
 * core components without a job running infrastructure in place
 */
@Slf4j
public class InMemoryTaskManager implements TaskManager {

    @Inject
    Injector injector;

    @Override
    public void start() {
        // No startup required
    }

    @Override
    public void submit(Task task) {
        log.info("Submitted task " + task);
        TaskStep step = task.getStep();
        log.info("Creating task step for " + task);
        injector.injectMembers(step);
        step.rehydrate();
        log.info("Start task step for " + task);
        TaskStatus status;
        Exception lastException = null;
        try {
            status = step.call();
        } catch(Exception e) {
            status = TaskStatus.FAILED;
            lastException = e;
            log.error("Task step exception",e);
        }
        task.setStatus(status);
        log.info("Final status "+status+" for "+task);
        if ( lastException != null ) {
            throw new SkyeException(task+" failed",lastException);
        }
    }

    @Override
    public void toLog(Task task, String message) {
        // Not implemented for in memory task manager
    }

    @Override
    public void toLog(Task task, String message, Exception e) {
        // Not implemented for in memory task manager
    }
}
