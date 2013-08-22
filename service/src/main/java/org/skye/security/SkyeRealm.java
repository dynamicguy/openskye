package org.skye.security;

import com.yammer.dropwizard.hibernate.UnitOfWork;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;
import org.skye.core.SkyeException;
import org.skye.resource.dao.UserDAO;

import javax.inject.Inject;

/**
 * SkyeRealm: a realm specific to Skye
 */
public class SkyeRealm extends AuthenticatingRealm {

    @Inject
    private UserDAO userDao;
    @Inject
    private SessionFactory sessionFactory;

    public SkyeRealm() {
        super();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        final Session session = sessionFactory.openSession();
        try {

            ManagedSessionContext.bind(session);
            try {
                // TODO so we need to look up the user using hibernate
                // by adding the methods to find the username
                // and check the
                userDao.list();

                throw new AuthenticationException();
            } catch (Exception e) {
                throw new SkyeException("Unable to authenication user", e);
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
