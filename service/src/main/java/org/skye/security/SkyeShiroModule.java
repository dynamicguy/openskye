package org.skye.security;

import com.google.inject.Exposed;
import com.google.inject.Provides;
import com.yammer.dropwizard.hibernate.HibernateBundle;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.hibernate.SessionFactory;
import org.skye.config.SkyeConfiguration;
import org.skye.util.CreateDefaultAccount;

import javax.servlet.ServletContext;

/**
 * Skye's Shiro Module
 */
public class SkyeShiroModule extends ShiroWebModule {

    private final HibernateBundle<SkyeConfiguration> hibernate;

    public SkyeShiroModule(ServletContext sc, HibernateBundle<SkyeConfiguration> hibernate) {
        super(sc);
        this.hibernate = hibernate;
    }

    @Override
    protected void configureShiroWeb() {
        bindRealm().to(SkyeRealm.class).asEagerSingleton();
        bind(CreateDefaultAccount.class).asEagerSingleton();
        addFilterChain("/api/**", NO_SESSION_CREATION, AUTHC_BASIC);
        ShiroWebModule.bindGuiceFilter(binder());
    }

    @Provides
    @Exposed
    public SessionFactory provideSessionFactory() {
        return hibernate.getSessionFactory();
    }

}