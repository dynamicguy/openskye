package org.skye;

import com.google.inject.Provides;
import com.yammer.dropwizard.hibernate.HibernateBundle;
import org.apache.shiro.config.Ini;
import org.apache.shiro.guice.ShiroModule;
import org.hibernate.SessionFactory;
import org.skye.config.SkyeConfiguration;
import org.skye.metadata.ObjectMetadataRepository;
import org.skye.metadata.impl.InMemoryObjectMetadataRepository;
import org.skye.security.SkyeRealm;
import org.skye.stores.StoreRegistry;

/**
 * A basic module for Skye using the testing components
 */
public class SkyeTestModule extends ShiroModule {

    private final HibernateBundle<SkyeConfiguration> hibernate;

    public SkyeTestModule(HibernateBundle<SkyeConfiguration> hibernate) {
        this.hibernate = hibernate;
    }

    @Provides
    public SessionFactory provideSessionFactory() {
        return hibernate.getSessionFactory();
    }

    @Override
    public void configure() {
        bind(ObjectMetadataRepository.class).to(InMemoryObjectMetadataRepository.class).asEagerSingleton();
        bind(StoreRegistry.class).asEagerSingleton();
    }

    @Override
    protected void configureShiro(){
        try{
            bindRealm().toConstructor(SkyeRealm.class.getConstructor(SkyeRealm.class));
        }
        catch (NoSuchMethodException e){
             addError(e);
        }
    }

}