package org.openskye.task.simple;

import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
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
        log.info("Start task step for " + task);
        TaskStatus status;
        try {
            status = step.call();
        } catch(Exception e) {
            status = TaskStatus.FAILED;
            log.error("Task step exception",e);
        }
        log.info("Final status "+status+" for "+task);
    }


}
