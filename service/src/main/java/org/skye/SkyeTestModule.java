package org.skye;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.yammer.dropwizard.hibernate.HibernateBundle;
import org.hibernate.SessionFactory;
import org.skye.config.SkyeConfiguration;

/**
 * A basic module for Skye using the testing components
 */
public class SkyeTestModule extends AbstractModule {

    private final HibernateBundle<SkyeConfiguration> hibernate;

    public SkyeTestModule(HibernateBundle<SkyeConfiguration> hibernate) {
        this.hibernate = hibernate;
    }

    @Provides
    public SessionFactory provideSessionFactory() {
        return hibernate.getSessionFactory();
    }

    @Override
    protected void configure() {

    }

}