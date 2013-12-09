package org.openskye.security;

import com.google.common.base.Optional;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.mindrot.jbcrypt.BCrypt;
import org.openskye.domain.*;
import org.openskye.domain.dao.ProjectUserDAO;
import org.openskye.domain.dao.UserDAO;

import javax.inject.Inject;
import java.util.List;


/**
 * SkyeRealm: a realm specific to Skye with basic auth and/or API key security
 */
public class SkyeRealm extends AuthorizingRealm {

    @Inject
    private UserDAO userDao;

    @Inject
    private ProjectUserDAO projectUserDAO;

    public SkyeRealm() {
        super();
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        User u = (User) getAvailablePrincipal(principals);
        Optional<List<ProjectUser>> pu = projectUserDAO.findByUser(u.getId());
        SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo();
        for (UserRole role : u.getUserRoles()) {
            authInfo.addRole((role.getRole().getName()));
            for (RolePermission rp : role.getRole().getRolePermissions()) {
                authInfo.addStringPermission(rp.getPermission().getPermission());
            }
        }
        if(pu.isPresent()){
            for(ProjectUser pUser : pu.get()){
                for(Role pRole : pUser.getProjectUserRoles()){
                    authInfo.addRole(pRole.getName());
                    for(RolePermission rp : pRole.getRolePermissions()){
                        authInfo.addStringPermission(rp.getPermission().getPermission()+":"+pUser.getProject().getId());
                    }
                }
            }

        }

        return authInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        if (authenticationToken instanceof UsernamePasswordToken) {
            UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
            Optional<User> user = userDao.findByEmail(token.getUsername());
            if (user.isPresent()) { //user is found
                String pwd = new String(token.getPassword());
                Boolean passwordsMatch = BCrypt.checkpw(pwd, user.get().getPasswordHash());
                if (passwordsMatch) { //user has correct password
                    SimpleAuthenticationInfo simpleAuthInfo = new SimpleAuthenticationInfo(user.get(), token.getPassword(), this.getName());
                    return simpleAuthInfo;
                } else {
                    throw new AuthenticationException();
                }
            } else {
                throw new AuthenticationException();
            }
        } else if (authenticationToken instanceof ApiKeyToken) {
            ApiKeyToken token = (ApiKeyToken) authenticationToken;
            Optional<User> user = userDao.findByApiKey(token.getKey());
            if (user.isPresent()) { //user is found
                return new SimpleAuthenticationInfo(user.get(), token.getKey(), this.getName());
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
        return (authenticationToken instanceof UsernamePasswordToken
                || authenticationToken instanceof ApiKeyToken);
    }

}
