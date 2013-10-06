package org.skye.security;

import com.google.inject.Key;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.skye.util.CreateDefaultAccount;

import javax.servlet.ServletContext;

/**
 * Skye's Shiro Module for the BASIC_AUTH security model.  Each API call requires a user name and password,
 * and is separately authenticated against Skye's own user/password database.
 */
public class SkyeShiroModule extends ShiroWebModule {

    public static final Key<SkyeAuthenticatingFilter> SKYE_AUTHENTICATING_FILTER_KEY = Key.get(SkyeAuthenticatingFilter.class);

    public SkyeShiroModule(ServletContext sc) {
        super(sc);
    }

    @Override
    protected void configureShiroWeb() {
        bindRealm().to(SkyeRealm.class).asEagerSingleton();
        bind(CreateDefaultAccount.class).asEagerSingleton();
        bind(CacheManager.class).to(MemoryConstrainedCacheManager.class).asEagerSingleton();
        addFilterChain("/api/**", NO_SESSION_CREATION, SKYE_AUTHENTICATING_FILTER_KEY);
        ShiroWebModule.bindGuiceFilter(binder());
    }

}