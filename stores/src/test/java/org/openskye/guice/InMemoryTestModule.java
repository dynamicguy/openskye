package org.openskye.guice;

import com.google.guiceberry.GuiceBerryModule;
import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;
import org.openskye.metadata.impl.InMemoryObjectMetadataSearch;
import org.openskye.metadata.impl.jpa.JPAObjectMetadataRepository;
import org.openskye.stores.StoreRegistry;
import org.openskye.task.TaskManager;
import org.openskye.task.TaskScheduler;
import org.openskye.task.simple.InMemoryTaskManager;
import org.openskye.task.simple.InMemoryTaskScheduler;

import java.util.Properties;

public class InMemoryTestModule extends AbstractModule {
    @Override
    protected void configure() {
        JpaPersistModule jpaPersistModule = new JpaPersistModule("Default");
        Properties props = new Properties();
        props.put("javax.persistence.jdbc.url", "jdbc:h2:mem:openskye-test");
        props.put("javax.persistence.jdbc.user", "sa");
        props.put("javax.persistence.jdbc.password", "");
        props.put("javax.persistence.jdbc.driver", "org.h2.Driver");
        props.put("hibernate.hbm2ddl.auto", "create");
        props.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");

        jpaPersistModule.properties(props);
        install(jpaPersistModule);

        bind(TaskManager.class).to(InMemoryTaskManager.class).asEagerSingleton();
        bind(TaskScheduler.class).to(InMemoryTaskScheduler.class).asEagerSingleton();
        bind(StoreRegistry.class).asEagerSingleton();
        bind(ObjectMetadataRepository.class).to(JPAObjectMetadataRepository.class);
        bind(ObjectMetadataSearch.class).to(InMemoryObjectMetadataSearch.class);

        install(new GuiceBerryModule());
    }
}
