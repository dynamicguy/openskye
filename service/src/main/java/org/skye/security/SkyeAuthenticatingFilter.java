package org.skye.security;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Extract the API key from an HTTP request header
 */
public class SkyeAuthenticatingFilter extends BasicHttpAuthenticationFilter {

    public static final String X_API_KEY = "x-api-key";
    public static final String QUERY_API_KEY = "api_key";

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        AuthenticationToken token = createApiToken(request, response);
        if (token == null) {
            // Fall through to basic auth
            return super.onAccessDenied(request, response);
        }
        try {
            Subject subject = getSubject(request, response);
            subject.login(token);
            return onLoginSuccess(token, subject, request, response);
        } catch (AuthenticationException e) {
            return onLoginFailure(token, e, request, response);
        }

    }

    protected AuthenticationToken createApiToken(ServletRequest request, ServletResponse response) {
        if (request.getParameter(QUERY_API_KEY) != null) {
            return new ApiKeyToken(request.getParameter(QUERY_API_KEY));
        } else if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = WebUtils.toHttp(request);
            if (httpRequest.getHeader(X_API_KEY) != null)
                return new ApiKeyToken(httpRequest.getHeader(X_API_KEY));
        }

        // We can't construct an API token so don't return one
        return null;
    }
}
