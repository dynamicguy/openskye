package org.skye.security;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.skye.util.CreateDefaultAccount;

import javax.servlet.ServletContext;

/**
 * Skye's Shiro Module for the SSL security model.  Each API call requires a user name but not a password,
 * and must be sent over SSL using a custom certificate.
 */
public class SkyeSslShiroModule extends ShiroWebModule {

    public SkyeSslShiroModule(ServletContext sc) {
        super(sc);
    }

    @Override
    protected void configureShiroWeb() {
        bindRealm().to(SkyeSslRealm.class).asEagerSingleton();
        bind(CreateDefaultAccount.class).asEagerSingleton();
        bind(CacheManager.class).to(MemoryConstrainedCacheManager.class).asEagerSingleton();
        addFilterChain("/api/**", NO_SESSION_CREATION, AUTHC_BASIC); // Note: Shiro SSL filter not needed here
                                                                     // SSL support already provided by Dropwizard
        ShiroWebModule.bindGuiceFilter(binder());
    }

}
