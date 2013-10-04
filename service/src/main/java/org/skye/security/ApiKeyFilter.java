package org.skye.security;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Extract the API key from an HTTP request header
 */
public class ApiKeyFilter extends AuthenticatingFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        return executeLogin(servletRequest, servletResponse);
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        if (request.getParameter("apikey") != null) {
            return new ApiKeyToken(request.getParameter("apikey"));
        }
        return new ApiKeyToken("");
    }
}
