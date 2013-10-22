package org.openskye.metadata.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.guiceberry.GuiceBerryModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.elasticsearch.client.Client;
import static org.elasticsearch.node.NodeBuilder.*;
import org.openskye.core.SkyeSession;
import org.openskye.domain.Domain;
import org.openskye.domain.User;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;
import org.openskye.metadata.impl.InMemoryObjectMetadataRepository;
import org.openskye.stores.StoreRegistry;
import org.openskye.task.TaskManager;
import org.openskye.task.simple.InMemoryTaskManager;

import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: joshua
 * Date: 10/16/13
 * Time: 9:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class InMemoryTestModule extends AbstractModule
{
    protected static final String DOMAIN_ID = "domain1";
    protected static final String USER_ID = "user1";
    protected static final String USER_NAME = "user";
    protected static Client client;

    @Override
    protected void configure()
    {
        bind(TaskManager.class).to(InMemoryTaskManager.class).asEagerSingleton();
        bind(StoreRegistry.class).asEagerSingleton();
        bind(ObjectMetadataRepository.class).to(InMemoryObjectMetadataRepository.class).asEagerSingleton();
        bind(ObjectMetadataSearch.class).to(ElasticSearchObjectMetadataSearch.class).asEagerSingleton();

        install(new GuiceBerryModule());
    }

    @Provides
    @Singleton
    protected ObjectMapper provideObjectMapper()
    {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JodaModule());

        return mapper;
    }

    @Provides
    @Singleton
    protected Client provideClient()
    {
        if(this.client == null)
            this.client = nodeBuilder().local(true).node().client();

        return this.client;
    }

    @Provides
    @Singleton
    protected SkyeSession provideSession()
    {
        SkyeSession session = mock(SkyeSession.class);
        Domain domain = new Domain();
        User user = new User();

        domain.setId(DOMAIN_ID);

        user.setId(USER_ID);
        user.setName(USER_NAME);
        user.setDomain(domain);

        when(session.getUser()).thenReturn(user);
        when(session.getDomain()).thenReturn(domain);

        return session;
    }
}
