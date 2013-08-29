package org.skye.security;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * An implementation
 */
public class SkyeGuiceServletContextListener extends GuiceServletContextListener {
    private final JpaPersistModule jpaPersistModule;
    private ServletContext servletContext;

    public SkyeGuiceServletContextListener(JpaPersistModule jpaPersistModule) {
        this.jpaPersistModule = jpaPersistModule;
    }

    @Override
    protected Injector getInjector() {
        Injector childInjector = Guice.createInjector(jpaPersistModule, new SkyeShiroModule(servletContext));
        return childInjector;
    }

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        this.servletContext = servletContextEvent.getServletContext();
        super.contextInitialized(servletContextEvent);
    }
}