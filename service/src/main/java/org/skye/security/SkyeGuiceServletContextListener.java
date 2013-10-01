package org.skye.security;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.skye.core.SkyeException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * An implementation
 */
public class SkyeGuiceServletContextListener extends GuiceServletContextListener {
    private final JpaPersistModule jpaPersistModule;
    private ServletContext servletContext;
    private String securityModel;

    public SkyeGuiceServletContextListener(JpaPersistModule jpaPersistModule,String securityModel) {
        this.jpaPersistModule = jpaPersistModule;
        this.securityModel = securityModel;
    }

    @Override
    protected Injector getInjector() {
        ShiroWebModule shiroWebModule = null;
        if ( securityModel.equals("ANON") ) {
            shiroWebModule = new SkyeAnonShiroModule(servletContext);
        } else if ( securityModel.equals("BASIC_AUTH") ) {
            shiroWebModule = new SkyeBasicAuthShiroModule(servletContext);
        } else if ( securityModel.equals("SSL") ) {
            shiroWebModule = new SkyeSslShiroModule(servletContext);
        } else {
            throw new SkyeException("Unsupported security model "+securityModel);
        }
        Injector childInjector = Guice.createInjector(jpaPersistModule, shiroWebModule);
        return childInjector;
    }

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        this.servletContext = servletContextEvent.getServletContext();
        super.contextInitialized(servletContextEvent);
    }
}