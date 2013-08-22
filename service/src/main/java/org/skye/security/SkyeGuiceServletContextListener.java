package org.skye.security;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * An implementation
 */
public class SkyeGuiceServletContextListener extends GuiceServletContextListener {
    private ServletContext servletContext;
    private Injector injector;

    public SkyeGuiceServletContextListener(Injector injector) {
        this.injector = injector;
    }

    @Override
    protected Injector getInjector() {
        Injector childInjector = Guice.createInjector(new SkyeShiroModule(servletContext));
        return childInjector;
    }

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        this.servletContext = servletContextEvent.getServletContext();
        super.contextInitialized(servletContextEvent);
    }
}