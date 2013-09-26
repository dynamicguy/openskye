package org.skye.metadata.impl.jpa;

import com.google.guiceberry.GuiceBerryModule;
import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.skye.domain.dao.*;
import org.skye.metadata.ObjectMetadataRepository;
import org.skye.metadata.ObjectMetadataSearch;
import org.skye.metadata.impl.InMemoryObjectMetadataSearch;
import org.skye.stores.StoreRegistry;
import org.skye.task.TaskManager;
import org.skye.task.simple.InMemoryTaskManager;

import java.util.Properties;

public class InMemoryTestModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        JpaPersistModule jpaModule = new JpaPersistModule("Default");
        Properties props = new Properties();

        props.put("javax.persistence.jdbc.url", "jdbc:h2:mem:skye");
        props.put("javax.persistence.jdbc.user", "sa");
        props.put("javax.persistence.jdbc.password", "");
        props.put("javax.persistence.jdbc.driver", "org.h2.Driver");

        jpaModule.properties(props);

        install(jpaModule);

        bind(JpaInitializer.class).asEagerSingleton();
        bind(TaskManager.class).to(InMemoryTaskManager.class).asEagerSingleton();
        bind(StoreRegistry.class).asEagerSingleton();
        bind(ObjectMetadataRepository.class).to(JPAObjectMetadataRepository.class).asEagerSingleton();
        bind(ObjectMetadataSearch.class).to(InMemoryObjectMetadataSearch.class).asEagerSingleton();
        bind(InformationStoreDefinitionDAO.class).asEagerSingleton();
        bind(ArchiveStoreDefinitionDAO.class).asEagerSingleton();
        bind(TaskDAO.class).asEagerSingleton();
        bind(TaskStatisticsDAO.class).asEagerSingleton();
        bind(ArchiveStoreInstanceDAO.class).asEagerSingleton();

        install(new GuiceBerryModule());
    }
}
