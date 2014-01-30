package org.openskye.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.guiceberry.GuiceBerryModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.elasticsearch.client.Client;
import org.openskye.core.SkyeSession;
import org.openskye.domain.Domain;
import org.openskye.domain.User;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;
import org.openskye.metadata.elasticsearch.ElasticSearchObjectMetadataSearch;
import org.openskye.metadata.impl.jpa.JPAObjectMetadataRepository;
import org.openskye.stores.StoreRegistry;

import javax.inject.Singleton;
import java.util.Properties;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ObjectMetadataModule extends AbstractModule
{
    protected static final String DOMAIN_ID = "domain1";
    protected static final String USER_ID = "user1";
    protected static final String USER_NAME = "user";
    protected static Client client;

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

        bind(ObjectMetadataRepository.class).to(JPAObjectMetadataRepository.class).asEagerSingleton();
        bind(ObjectMetadataSearch.class).to(ElasticSearchObjectMetadataSearch.class).asEagerSingleton();
        bind(StoreRegistry.class).asEagerSingleton();

        bind(ObjectMetadataResource.class).asEagerSingleton();
        bind(ObjectSetResource.class).asEagerSingleton();

        install(new GuiceBerryModule());
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
    protected Client provideClient() {
        if (this.client == null)
            this.client = nodeBuilder().local(true).node().client();

        return this.client;
    }

    @Provides
    @Singleton
    protected SkyeSession provideSession() {
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
