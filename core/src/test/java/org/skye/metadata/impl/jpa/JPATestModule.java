package org.skye.metadata.impl.jpa;

import com.google.guiceberry.GuiceBerryModule;
import com.google.inject.AbstractModule;
import org.skye.metadata.ObjectMetadataRepository;
import org.skye.metadata.ObjectMetadataSearch;
import org.skye.metadata.impl.InMemoryObjectMetadataSearch;
import org.skye.stores.StoreRegistry;
import org.skye.task.TaskManager;
import org.skye.task.simple.InMemoryTaskManager;

public class JPATestModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(TaskManager.class).to(InMemoryTaskManager.class).asEagerSingleton();
        bind(StoreRegistry.class).asEagerSingleton();
        bind(ObjectMetadataRepository.class).to(JPAObjectMetadataRepository.class).asEagerSingleton();
        bind(ObjectMetadataSearch.class).to(InMemoryObjectMetadataSearch.class).asEagerSingleton();

        requestStaticInjection(JPAArchiveContentBlock.class);

        install(new GuiceBerryModule());
    }
}
