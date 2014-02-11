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

            if (httpRequest.getQueryString() != null) {
                log.debug("Got query string " + httpRequest.getQueryString());
            }

            String pageParam = httpRequest.getParameter("_page");
            if (pageParam != null) {
                try {
                    log.debug("Setting page to " + pageParam);
                    context.setPage(Integer.parseInt(pageParam));
                } catch (NumberFormatException ex) {
                    log.debug("Invalid _page param = " + pageParam);
                }
            }

            String pageSizeParam = httpRequest.getParameter("_pageSize");
            if (pageSizeParam != null) {
                try {
                    log.debug("Setting page size to " + pageSizeParam);
                    context.setPageSize(Integer.parseInt(pageSizeParam));
                } catch (NumberFormatException ex) {
                    log.debug("Invalid _page param = " + pageSizeParam);
                }
            }

            String sortParam = httpRequest.getParameter("_sort");
            if (sortParam != null) {
                log.debug("Setting sort to " + sortParam);
                context.setSort(sortParam);
            }

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
