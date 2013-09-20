package org.skye.task.simple;

import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.skye.core.SkyeException;
import org.skye.domain.Task;
import org.skye.task.TaskManager;

import javax.inject.Inject;

/**
 * An implementation of the {@link TaskManager} that operates in-memory
 * <p/>
 * NOTE: This is not thread-safe and is really in place to allow for testing of the
 * core components without a job running infrastructure in placeø
 */
@Slf4j
public class InMemoryTaskManager implements TaskManager {

    @Inject
    Injector injector;

    @Override
    public void submit(Task task) {
        log.info("Submitted task " + task);
        TaskStep newTask = getTaskStep(task);
        log.info("Creating task step for " + task);
        injector.injectMembers(newTask);
        log.info("Start task step for " + task);
        newTask.start();
    }

    public TaskStep getTaskStep(Task task) {
        switch (task.getTaskType()) {
            case ARCHIVE:
                return new ArchiveTaskStep(task);
            case DISCOVER:
                return new DiscoverTaskStep(task);
            case EXTRACT:
                return new ExtractTaskStep(task);
            case DESTROY:
                return new DestroyTaskStep(task);
            default:
                throw new SkyeException("Unsupported task type " + task.getTaskType());
        }
    }
}
