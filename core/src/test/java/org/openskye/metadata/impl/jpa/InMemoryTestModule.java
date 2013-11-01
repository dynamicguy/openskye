package org.openskye.metadata.impl.jpa;

import com.google.guiceberry.GuiceBerryModule;
import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.openskye.domain.dao.*;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;
import org.openskye.metadata.impl.InMemoryObjectMetadataSearch;
import org.openskye.stores.StoreRegistry;
import org.openskye.task.TaskManager;
import org.openskye.task.simple.InMemoryTaskManager;

import java.util.Properties;

public class InMemoryTestModule extends AbstractModule {
    @Override
    protected void configure() {
        JpaPersistModule jpaModule = new JpaPersistModule("Default");
        Properties props = new Properties();

//        props.put("javax.persistence.jdbc.url", "jdbc:h2:mem:openskye");
//        props.put("javax.persistence.jdbc.user", "sa");
//        props.put("javax.persistence.jdbc.password", "");
//        props.put("javax.persistence.jdbc.driver", "org.h2.Driver");

        props.put("javax.persistence.jdbc.url", "jdbc:mysql://localhost/openskye");
        props.put("javax.persistence.jdbc.user", "root");
        props.put("javax.persistence.jdbc.password", "password");
        props.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");


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
