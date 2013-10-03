package org.skye.security;

import com.google.common.base.Optional;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.*;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.subject.WebSubject;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;
import org.apache.shiro.web.util.WebUtils;
import org.skye.domain.User;
import org.skye.domain.dao.UserDAO;

import javax.inject.Inject;
import javax.naming.AuthenticationException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Extract the API key from an HTTP request header
 */
public class ApiKeyFilter extends BasicHttpAuthenticationFilter {

    @Inject
    private UserDAO userDao;

    @Override
    protected Subject getSubject(ServletRequest request, ServletResponse response) {
        ApiKeyToken token = (ApiKeyToken) createToken(request,response);
        Optional<User> user = userDao.findByEmail(token.getUsername());
        PrincipalCollection principals = new SimplePrincipalCollection(user, "ApiKeyRealm");
        WebDelegatingSubject subject = new WebDelegatingSubject( principals, true, getHost(request), null, false,
            request, response, SecurityUtils.getSecurityManager() );
        return subject;
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        String key = httpRequest.getHeader("x-api-key");
        return new ApiKeyToken(key);
    }
}
