package org.openskye.task.simple;

import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.SkyeException;
import org.openskye.domain.Task;
import org.openskye.task.TaskManager;
import org.openskye.task.quartz.AbstractQuartzTaskManager;
import org.openskye.task.step.AbstractTaskStep;
import org.openskye.task.step.TaskStep;

import javax.inject.Inject;

/**
 * An implementation of the {@link TaskManager} that operates in-memory
 * <p/>
 * NOTE: This is not thread-safe and is really in place to allow for testing of the
 * core components without a job running infrastructure in placeø
 */
@Slf4j
public class InMemoryTaskManager extends AbstractQuartzTaskManager {

    @Inject
    Injector injector;

    @Override
    public void submit(Task task) {
        log.info("Submitted task " + task);
        TaskStep newTask = AbstractTaskStep.fromTask(task);
        log.info("Creating task step for " + task);
        injector.injectMembers(newTask);
        log.info("Start task step for " + task);
        newTask.start();
    }


}
