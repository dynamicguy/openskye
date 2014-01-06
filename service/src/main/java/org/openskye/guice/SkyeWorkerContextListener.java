package org.openskye.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import org.apache.shiro.guice.web.ShiroWebModule;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * User: atcmostafavi Date: 1/6/14 Time: 10:53 AM Project: platform
 */
public class SkyeWorkerContextListener extends GuiceServletContextListener {
    private JpaPersistModule jpaPersistModule;
    private ServletContext servletContext;

    public SkyeWorkerContextListener(JpaPersistModule jpaPersistModule) {
        this.jpaPersistModule = jpaPersistModule;
    }

    @Override
    protected Injector getInjector(){
        ShiroWebModule shiroWebModule = new SkyeWorkerShiroModule(servletContext);
        Injector childInjector = Guice.createInjector(jpaPersistModule, shiroWebModule);
        return childInjector;
    }

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        this.servletContext = servletContextEvent.getServletContext();
        super.contextInitialized(servletContextEvent);
    }
}
