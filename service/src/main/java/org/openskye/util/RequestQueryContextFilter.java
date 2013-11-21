package org.openskye.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.util.WebUtils;
import org.openskye.query.RequestQueryContext;
import org.openskye.query.RequestQueryContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * An implementation of a filter
 */
@Slf4j
public class RequestQueryContextFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to do
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        RequestQueryContext context = new RequestQueryContext();
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = WebUtils.toHttp(servletRequest);
            if (httpRequest.getParameter("_page") != null) {
                try {
                    context.setPage(Integer.parseInt(httpRequest.getParameter("_page")));
                } catch (NumberFormatException ex) {
                    log.debug("Invalid _page param = " + httpRequest.getParameter("_page"));
                }
            }
            if (httpRequest.getParameter("_pageSize") != null) {
                try {
                    context.setPageSize(Integer.parseInt(httpRequest.getParameter("_pageSize")));
                } catch (NumberFormatException ex) {
                    log.debug("Invalid _page param = " + httpRequest.getParameter("_pageSize"));
                }
            }

            context.setSort(httpRequest.getParameter("_sort"));

        }
        RequestQueryContextHolder.setContext(context);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // Nothing to do
    }
}
