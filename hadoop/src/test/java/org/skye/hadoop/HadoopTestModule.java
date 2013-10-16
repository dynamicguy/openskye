package org.skye.hadoop;

import com.google.guiceberry.GuiceBerryModule;
import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.skye.domain.dao.ArchiveStoreDefinitionDAO;
import org.skye.domain.dao.ArchiveStoreInstanceDAO;
import org.skye.domain.dao.TaskDAO;
import org.skye.domain.dao.TaskStatisticsDAO;
import org.skye.hadoop.metadata.HBaseObjectMetadataRepository;
import org.skye.hadoop.metadata.HBaseObjectMetadataSearch;
import org.skye.hadoop.task.HadoopTaskManager;
import org.skye.metadata.ObjectMetadataRepository;
import org.skye.metadata.ObjectMetadataSearch;
import org.skye.task.TaskManager;

/**
 * This is a Guice module used for Hadoop Testing
 */
public class HadoopTestModule extends AbstractModule {

    @Override
    protected void configure() {
        JpaPersistModule jpaModule = new JpaPersistModule("hbase");
        install(jpaModule);
        bind(HBaseJPAInitializer.class).asEagerSingleton();
        bind(ObjectMetadataRepository.class).to(HBaseObjectMetadataRepository.class).asEagerSingleton();
        bind(TaskManager.class).to(HadoopTaskManager.class).asEagerSingleton();
        bind(ObjectMetadataSearch.class).to(HBaseObjectMetadataSearch.class).asEagerSingleton();
        bind(ArchiveStoreDefinitionDAO.class).asEagerSingleton();
        bind(TaskDAO.class).asEagerSingleton();
        bind(TaskStatisticsDAO.class).asEagerSingleton();
        bind(ArchiveStoreInstanceDAO.class).asEagerSingleton();

        install(new GuiceBerryModule());
    }
}
