package org.openskye.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.openskye.config.SkyeConfiguration;
import org.openskye.core.SkyeException;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;
import org.openskye.stores.StoreRegistry;
import org.openskye.task.TaskManager;
import org.openskye.task.TaskScheduler;

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
            bind(StoreRegistry.class).asEagerSingleton();
            Class taskManagerClazz = Class.forName(skyeConfiguration.getServices().getTaskManager());
            bind(TaskManager.class).to(taskManagerClazz);
            Class taskSchedulerClazz = Class.forName(skyeConfiguration.getServices().getTaskScheduler());
            bind(TaskScheduler.class).to(taskSchedulerClazz);
            Class omrClazz = Class.forName(skyeConfiguration.getServices().getOmr());
            bind(ObjectMetadataRepository.class).to(omrClazz).asEagerSingleton();
            Class omsClazz = Class.forName(skyeConfiguration.getServices().getOms());
            bind(ObjectMetadataSearch.class).to(omsClazz).asEagerSingleton();
        } catch (ClassNotFoundException e) {
            throw new SkyeException("Unable to bind services, check your configuration", e);
        }

    }


    @Provides
    @Singleton
    protected ObjectMapper provideObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JodaModule());

        return mapper;
    }

    @Provides
    @Singleton
    protected Client provideClient()
    {
        Client client = new TransportClient()
                            .addTransportAddress(
                                    new InetSocketTransportAddress(
                                            "localhost",
                                            9300
                                    )
                            );

        return client;
    }
}
