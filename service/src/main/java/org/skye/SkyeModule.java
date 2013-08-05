package org.skye;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.yammer.dropwizard.hibernate.HibernateBundle;
import org.hibernate.SessionFactory;
import org.skye.config.SkyeConfiguration;

/**
 * Guice module for Skye
 */
public class SkyeModule extends AbstractModule {

    private final HibernateBundle<SkyeConfiguration> hibernate;

    public SkyeModule(HibernateBundle<SkyeConfiguration> hibernate) {
        this.hibernate = hibernate;
    }

    @Override
    protected void configure() {

    }

    @Provides
    SessionFactory provideSessionFactory() {
        return hibernate.getSessionFactory();
    }
}
