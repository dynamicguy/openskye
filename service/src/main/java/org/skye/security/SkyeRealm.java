package org.skye.security;

import com.google.common.base.Optional;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.mindrot.jbcrypt.BCrypt;
import org.skye.domain.Permission;
import org.skye.domain.User;
import org.skye.domain.UserRole;
import org.skye.resource.dao.UserDAO;

import javax.inject.Inject;
import java.util.*;


/**
 * SkyeRealm: a realm specific to Skye
 */
public class SkyeRealm extends AuthorizingRealm {

    @Inject
    private UserDAO userDao;

    public SkyeRealm() {
        super();
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        User u = (User)getAvailablePrincipal(principals);
        List<UserRole> roles = u.getUserRoles();
        Iterator<UserRole> rolesIterator = roles.iterator();
        SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo();
        while(rolesIterator.hasNext()){
            UserRole current = rolesIterator.next();
            authInfo.addRole((current.getRole().getName()));
            Iterator<Permission> permissionIterator = current.getRole().getPermissions().iterator();
            while(permissionIterator.hasNext()){
                authInfo.addStringPermission(permissionIterator.next().getPermission());
            }
        }
        //authInfo.addStringPermission("*");
        return authInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        if (authenticationToken instanceof UsernamePasswordToken) {
            UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
            Optional<User> user = userDao.findByEmail(token.getUsername());
            String pwd = new String(token.getPassword());
            Boolean passwordsMatch = BCrypt.checkpw(pwd, user.get().getPasswordHash());
            if (user.isPresent() && passwordsMatch) { //user is found and their password matches
                SimpleAuthenticationInfo simpleAuthInfo = new SimpleAuthenticationInfo(user.get(), token.getPassword(), this.getName());
                return simpleAuthInfo;
            } else {
                throw new AuthenticationException();
            }
        } else {
            throw new AuthenticationException();
        }
    }

    @Override
    public String getName() {
        return "SkyeRealm";
    }

    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof UsernamePasswordToken;
    }

}
