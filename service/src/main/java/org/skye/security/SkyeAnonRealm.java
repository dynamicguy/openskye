package org.skye.security;


import com.google.common.base.Optional;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.mindrot.jbcrypt.BCrypt;
import org.skye.domain.RolePermission;
import org.skye.domain.User;
import org.skye.domain.UserRole;
import org.skye.domain.dao.UserDAO;

import javax.inject.Inject;

/**
 * SkyeAnonRealm: a realm specific to Skye with ANON security model, security disabled for
 * testing and development purposes.
 */
public class SkyeAnonRealm extends AuthorizingRealm {

    @Inject
    private UserDAO userDao;

    public SkyeAnonRealm() {
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
        Optional<User> user = userDao.findByEmail("admin@skye.org");
        String password = "changeme";
        return new SimpleAuthenticationInfo(user.get(), password, this.getName());
    }

    @Override
    public String getName() {
        return "SkyeAnonRealm";
    }

    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return true;
    }

}
