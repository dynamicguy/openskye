package org.openskye.guice;

import com.google.inject.AbstractModule;
import org.openskye.task.TaskManager;
import org.openskye.task.simple.InMemoryTaskManager;

/**
 * The Guice module for Skye
 */
public class SkyeModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TaskManager.class).to(InMemoryTaskManager.class).asEagerSingleton();
    }
}
