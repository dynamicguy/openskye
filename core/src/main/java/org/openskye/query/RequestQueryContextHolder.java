package org.openskye.query;

/**
 * This is a little thread-local context that we can use to store a few useful things that might well
 * be around on queries such as
 * <p/>
 * _sort        the field by which you want to sort
 * _filter      a very simple filter
 * _pageNum     the page number
 * _pageSize    the page size
 * <p/>
 * Which is all held in the {@link RequestQueryContext}
 */
public class RequestQueryContextHolder {

    private static ThreadLocal<RequestQueryContext> queryContextThreadLocal = new ThreadLocal<>();

    static {
        queryContextThreadLocal.set(new RequestQueryContext());
    }

    public static RequestQueryContext getContext() {
        return queryContextThreadLocal.get();
    }

    public static void setContext(RequestQueryContext context) {
        queryContextThreadLocal.set(context);
    }
}
