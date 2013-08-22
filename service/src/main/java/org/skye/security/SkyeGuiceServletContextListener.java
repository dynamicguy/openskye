package org.skye.security;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.yammer.dropwizard.hibernate.HibernateBundle;
import org.skye.config.SkyeConfiguration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * An implementation
 */
public class SkyeGuiceServletContextListener extends GuiceServletContextListener {
    private ServletContext servletContext;
    private HibernateBundle<SkyeConfiguration> hibernate;

    public SkyeGuiceServletContextListener(HibernateBundle<SkyeConfiguration> hibernate) {
        this.hibernate = hibernate;
    }

    @Override
    protected Injector getInjector() {
        Injector childInjector = Guice.createInjector(new SkyeShiroModule(servletContext,hibernate));
        return childInjector;
    }

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        this.servletContext = servletContextEvent.getServletContext();
        super.contextInitialized(servletContextEvent);
    }
}