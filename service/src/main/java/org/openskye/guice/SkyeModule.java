package org.openskye.guice;

import com.google.inject.AbstractModule;
import org.openskye.config.SkyeConfiguration;
import org.openskye.core.SkyeException;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;
import org.openskye.task.TaskManager;

/**
 * The Guice module for Skye
 */
public class SkyeModule extends AbstractModule {

    private final SkyeConfiguration skyeConfiguration;

    public SkyeModule(SkyeConfiguration skyeConfiguration) {
        this.skyeConfiguration = skyeConfiguration;
    }

    @Override
    protected void configure() {
        try {
            Class taskManagerClazz = Class.forName(skyeConfiguration.getServices().getTaskManager());
            bind(TaskManager.class).to(taskManagerClazz);
            Class omrClazz = Class.forName(skyeConfiguration.getServices().getOmr());
            bind(ObjectMetadataRepository.class).to(omrClazz).asEagerSingleton();
            Class omsClazz = Class.forName(skyeConfiguration.getServices().getOms());
            bind(ObjectMetadataSearch.class).to(omsClazz).asEagerSingleton();
        } catch (ClassNotFoundException e) {
            throw new SkyeException("Unable to bind services, check your configuration", e);
        }

    }
}
