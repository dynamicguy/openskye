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

    public static final Key<ApiKeyFilter> API_KEY = Key.get(ApiKeyFilter.class);

    public SkyeShiroModule(ServletContext sc) {
        super(sc);
    }

    @Override
    protected void configureShiroWeb() {
        bindRealm().to(SkyeBasicAuthRealm.class).asEagerSingleton();
        bindRealm().to(ApiKeyRealm.class).asEagerSingleton();
        bind(CreateDefaultAccount.class).asEagerSingleton();
        bind(CacheManager.class).to(MemoryConstrainedCacheManager.class).asEagerSingleton();
        addFilterChain("/api/*/account", NO_SESSION_CREATION, AUTHC_BASIC);
        addFilterChain("/api/*/account/key", NO_SESSION_CREATION, AUTHC_BASIC);
        addFilterChain("/api/**", NO_SESSION_CREATION, API_KEY);
        ShiroWebModule.bindGuiceFilter(binder());
    }

}