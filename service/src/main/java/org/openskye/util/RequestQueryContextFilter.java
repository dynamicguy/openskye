package org.openskye.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.util.WebUtils;
import org.openskye.query.RequestQueryContext;
import org.openskye.query.RequestQueryContextHolder;
import org.openskye.query.SortDirection;

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

            if (httpRequest.getQueryString() != null)
                log.debug("Got query string " + httpRequest.getQueryString());

            if (httpRequest.getParameter("_page") != null) {
                try {
                    log.debug("Setting page to " + httpRequest.getParameter("_page"));
                    context.setPage(Integer.parseInt(httpRequest.getParameter("_page")));
                } catch (NumberFormatException ex) {
                    log.debug("Invalid _page param = " + httpRequest.getParameter("_page"));
                }
            }
            if (httpRequest.getParameter("_pageSize") != null) {
                try {
                    log.debug("Setting page size to " + httpRequest.getParameter("_pageSize"));
                    context.setPageSize(Integer.parseInt(httpRequest.getParameter("_pageSize")));
                } catch (NumberFormatException ex) {
                    log.debug("Invalid _page param = " + httpRequest.getParameter("_pageSize"));
                }
            }

            if (httpRequest.getParameter("_sort") != null)
                log.debug("Setting sort to " + httpRequest.getParameter("_sort"));

            context.setSort(httpRequest.getParameter("_sort"));

            if ("desc".equals(httpRequest.getParameter("_sortDir"))) {
                log.debug("Setting sort direction to desc");
                context.setSortDir(SortDirection.DESC);
            }
        }
        RequestQueryContextHolder.setContext(context);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // Nothing to do
    }
}
