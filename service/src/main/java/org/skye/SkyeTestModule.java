package org.skye;

import com.google.inject.Provides;
import com.yammer.dropwizard.hibernate.HibernateBundle;
import org.apache.shiro.config.Ini;
import org.apache.shiro.guice.ShiroModule;
import org.apache.shiro.realm.Realm;
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
        configureShiro();
    }

    @Override
    protected void configureShiro(){
        Ini init = new Ini();
        init.addSection("main");
        init.setSectionProperty("main", "SkyeRealm", "org.skye.security.SkyeRealm");
        try{
            bindRealm().toConstructor(SkyeRealm.class.getConstructor(Ini.class));
        }
        catch (NoSuchMethodException e){
             addError(e);
        }
    }

}