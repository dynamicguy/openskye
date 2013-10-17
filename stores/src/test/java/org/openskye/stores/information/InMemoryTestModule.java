package org.openskye.stores.information;

import com.google.guiceberry.GuiceBerryModule;
import com.google.inject.AbstractModule;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;
import org.openskye.metadata.impl.InMemoryObjectMetadataRepository;
import org.openskye.metadata.impl.InMemoryObjectMetadataSearch;
import org.openskye.stores.StoreRegistry;
import org.openskye.task.TaskManager;
import org.openskye.task.simple.InMemoryTaskManager;

/**
 * A guice module that sets up everything for in-memory operation
 */
public class InMemoryTestModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TaskManager.class).to(InMemoryTaskManager.class).asEagerSingleton();
        bind(StoreRegistry.class).asEagerSingleton();
        bind(ObjectMetadataRepository.class).to(InMemoryObjectMetadataRepository.class).asEagerSingleton();
        bind(ObjectMetadataSearch.class).to(InMemoryObjectMetadataSearch.class).asEagerSingleton();

        install(new GuiceBerryModule());
    }
}
