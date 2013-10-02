package org.skye.security;

import com.google.common.base.Optional;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.skye.domain.RolePermission;
import org.skye.domain.User;
import org.skye.domain.UserRole;
import org.skye.domain.dao.UserDAO;

import javax.inject.Inject;

/**
 * Authenticates a previously issued API Key
 */
public class ApiKeyRealm extends AuthorizingRealm {

    @Inject
    private UserDAO userDao;

    public ApiKeyRealm() {
        super();
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        User u = (User) getAvailablePrincipal(principals);
        SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo();
        for (UserRole role : u.getUserRoles()) {
            authInfo.addRole((role.getRole().getName()));
            for (RolePermission rp : role.getRole().getRolePermissions()) {
                authInfo.addStringPermission(rp.getPermission().getPermission());
            }
        }

        return authInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        if (authenticationToken instanceof ApiKeyToken) {
            ApiKeyToken token = (ApiKeyToken) authenticationToken;
            Optional<User> user = userDao.findByEmail(token.getUsername());
            if (user.isPresent()) { //user is found
                if (token.isValid()) { //this is a valid API key
                    SimpleAuthenticationInfo simpleAuthInfo = new SimpleAuthenticationInfo(user.get(), token.getKey(), this.getName());
                    return simpleAuthInfo;
                } else {
                    throw new AuthenticationException();
                }
            } else {
                throw new AuthenticationException();
            }
        } else {
            throw new AuthenticationException();
        }
    }


    @Override
    public String getName() {
        return "ApiKeyRealm";
    }

    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof ApiKeyToken;
    }
}
