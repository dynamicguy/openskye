package org.skye.security;

import org.apache.shiro.web.env.MutableWebEnvironment;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;

import javax.inject.Inject;
import javax.servlet.ServletContext;

/**
 * An implementation of the Shiro web environment for Skye
 * <p/>
 * Using guice to wire it together
 */
public class SkyeWebEnvironment implements MutableWebEnvironment {

    @Inject
    private WebSecurityManager webSecurityManager;
    private ServletContext servletContext;
    private FilterChainResolver filterChainResolver;

    @Override
    public FilterChainResolver getFilterChainResolver() {
        return filterChainResolver;
    }

    @Override
    public void setFilterChainResolver(FilterChainResolver filterChainResolver) {
        this.filterChainResolver = filterChainResolver;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public WebSecurityManager getWebSecurityManager() {
        return webSecurityManager;
    }

    @Override
    public void setWebSecurityManager(WebSecurityManager webSecurityManager) {
        this.webSecurityManager = webSecurityManager;
    }

    @Override
    public org.apache.shiro.mgt.SecurityManager getSecurityManager() {
        return webSecurityManager;
    }
}
