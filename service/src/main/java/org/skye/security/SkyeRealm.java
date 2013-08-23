package org.skye.security;

import com.google.common.base.Optional;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;
import org.skye.core.SkyeException;
import org.skye.domain.User;
import org.skye.resource.dao.UserDAO;

import javax.inject.Inject;

/**
 * SkyeRealm: a realm specific to Skye
 */
public class SkyeRealm extends AuthorizingRealm {

    @Inject
    private UserDAO userDao;
    @Inject
    private SessionFactory sessionFactory;

    public SkyeRealm() {
        super();
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo();
        authInfo.addStringPermission("*");
        return authInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        final Session session = sessionFactory.openSession();
        try {
            ManagedSessionContext.bind(session);
            try {
                if (authenticationToken instanceof UsernamePasswordToken) {
                    UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
                    Optional<User> user = userDao.findByEmail(token.getUsername());
                    if (user.isPresent()) {
                        SimpleAuthenticationInfo simpleAuthInfo = new SimpleAuthenticationInfo(user.get(), token.getPassword(), this.getName());
                        return simpleAuthInfo;
                    } else {
                        throw new AuthenticationException();
                    }
                } else {
                    throw new AuthenticationException();
                }

            } catch (AuthenticationException e) {
                throw new SkyeException("Unable to authenticate user", e);
            }
        } finally {
            session.close();
            ManagedSessionContext.unbind(sessionFactory);
        }
    }

    @Override
    public String getName() {
        return "SkyeRealm";
    }

    @Override
    @UnitOfWork
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof UsernamePasswordToken;
    }


}
