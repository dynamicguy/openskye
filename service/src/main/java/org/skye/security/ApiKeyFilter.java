package org.skye.security;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

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
        } else if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = WebUtils.toHttp(request);
            return new ApiKeyToken(httpRequest.getHeader("x-api-key"));
        }
        return new ApiKeyToken("");
    }
}
