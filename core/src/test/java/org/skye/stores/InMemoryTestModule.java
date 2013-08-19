package org.skye.stores;

import com.google.inject.AbstractModule;
import org.skye.task.TaskManager;
import org.skye.task.inmemory.InMemoryTaskManager;

/**
 * A guice module that sets up everything for in-memory operation
 */
public class InMemoryTestModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TaskManager.class).to(InMemoryTaskManager.class);
    }
}
