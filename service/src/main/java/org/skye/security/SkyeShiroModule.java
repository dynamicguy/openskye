package org.skye.security;

import com.google.inject.Binder;
import org.apache.shiro.guice.web.ShiroWebModule;

import javax.servlet.ServletContext;

/**
 * Skye's Shiro Module
 */
public class SkyeShiroModule extends ShiroWebModule {

    public SkyeShiroModule(ServletContext sc) {
        super(sc);
    }

    @Override
    protected void configureShiroWeb() {
        bindRealm().to(SkyeRealm.class).asEagerSingleton();

        addFilterChain("/**", AUTHC_BASIC);
        ShiroWebModule.bindGuiceFilter(binder());
    }

}