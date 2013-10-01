package org.skye.security;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.skye.util.CreateDefaultAccount;

import javax.servlet.ServletContext;

/**
 * Skye's Shiro Module for the ANON security model, where all API calls are unsecured
 */
public class SkyeAnonShiroModule extends ShiroWebModule {

    public SkyeAnonShiroModule(ServletContext sc) {
        super(sc);
    }

    @Override
    protected void configureShiroWeb() {
        bindRealm().to(SkyeAnonRealm.class).asEagerSingleton();
        bind(CreateDefaultAccount.class).asEagerSingleton();
        bind(CacheManager.class).to(MemoryConstrainedCacheManager.class).asEagerSingleton();
        addFilterChain("/api/**", NO_SESSION_CREATION, ANON); // TODO: not allowing anonymous access as advertised
        ShiroWebModule.bindGuiceFilter(binder());
    }

}