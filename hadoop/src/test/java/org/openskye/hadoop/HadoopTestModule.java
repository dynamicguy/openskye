package org.openskye.hadoop;

import com.google.guiceberry.GuiceBerryModule;
import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.openskye.domain.dao.ArchiveStoreDefinitionDAO;
import org.openskye.domain.dao.ArchiveStoreInstanceDAO;
import org.openskye.domain.dao.TaskDAO;
import org.openskye.domain.dao.TaskStatisticsDAO;
import org.openskye.hadoop.metadata.HBaseObjectMetadataRepository;
import org.openskye.hadoop.metadata.HBaseObjectMetadataSearch;
import org.openskye.hadoop.task.HadoopTaskManager;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;
import org.openskye.task.TaskManager;

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
