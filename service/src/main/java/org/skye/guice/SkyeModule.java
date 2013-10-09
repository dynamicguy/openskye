package org.skye.guice;

import com.google.inject.AbstractModule;
import org.skye.task.TaskManager;
import org.skye.task.simple.InMemoryTaskManager;

/**
 * The Guice module for Skye
 */
public class SkyeModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TaskManager.class).to(InMemoryTaskManager.class).asEagerSingleton();
    }
}
