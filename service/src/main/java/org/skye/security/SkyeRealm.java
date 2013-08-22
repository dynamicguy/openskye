package org.skye.security;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.AuthenticatingRealm;

/**
 * SkyeRealm: a realm specific to Skye
 */
public class SkyeRealm extends AuthenticatingRealm {

    public SkyeRealm() {
        super();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        throw new AuthenticationException();
    }

    @Override
    public String getName() {
        return "SkyeRealm";
    }

    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return false;
    }


}
