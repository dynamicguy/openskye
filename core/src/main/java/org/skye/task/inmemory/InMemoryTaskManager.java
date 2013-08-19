package org.skye.task.inmemory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.skye.core.SkyeException;
import org.skye.domain.Task;
import org.skye.task.TaskManager;

/**
 * An implementation of the {@link TaskManager} that operates in-memory
 * <p/>
 * NOTE: This is not thread-safe and is really in place to allow for testing of the
 * core components without a job running infrastructure in placeø
 */
public class InMemoryTaskManager implements TaskManager {

    @Override
    public void submit(Task task) {
        Injector injector = Guice.createInjector();
        TaskStep newTask = getTaskStep(task);
        injector.injectMembers(newTask);
        newTask.start();
    }

    public TaskStep getTaskStep(Task task) {
        switch (task.getTaskType()) {
            case ARCHIVE:
                return new ArchiveTaskStep(task);
            case DISCOVER:
                return new DiscoverTaskStep(task);
            default:
                throw new SkyeException("Unsupported task type " + task.getTaskType());
        }
    }
}
