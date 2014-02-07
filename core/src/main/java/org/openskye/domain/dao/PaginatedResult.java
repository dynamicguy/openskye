package org.openskye.domain.dao;

import com.google.common.collect.Lists;
import lombok.Data;
import org.openskye.core.SearchPage;
import org.openskye.query.RequestQueryContext;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility to handle paginated results
 */
@Data
public class PaginatedResult<T> {

    private long page = 1;
    private long pageSize = 10;
    private long totalResults = 0;
    private List<T> results = new ArrayList<>();

    public PaginatedResult() {
    }

    public PaginatedResult(Iterable<T> list) {
        results = Lists.newArrayList(list);
        totalResults = results.size();
        pageSize = totalResults;
    }

    public PaginatedResult(Iterable<T> list, SearchPage searchPage)
    {
        results = Lists.newArrayList(list);
        totalResults = results.size();
        page = searchPage.getPageNumber();
        pageSize = searchPage.getPageSize();
    }

    public PaginatedResult(Iterable<T> list, RequestQueryContext context)
    {
        results = Lists.newArrayList(list);
        totalResults = results.size();
        page = context.getPage();
        pageSize = context.getPageSize();
    }

    public PaginatedResult(List<T> list) {
        results = list;
        totalResults = results.size();
        pageSize = totalResults;
    }

    public PaginatedResult(List<T> list, SearchPage searchPage)
    {
        results = list;
        totalResults = results.size();
        page = searchPage.getPageNumber();
        pageSize = searchPage.getPageSize();
    }

    public PaginatedResult(List<T> list, RequestQueryContext context)
    {
        results = list;
        totalResults = results.size();
        page = context.getPage();
        pageSize = context.getPageSize();
    }

    public PaginatedResult<T> paginate(List<T> list) {
        this.results = list;
        this.totalResults = results.size();
        pageSize = getPageSize();

        return this;
    }

    @Override
    public boolean equals(Object o) {

        // TODO: Fix PaginatedResult.equals(Object) check.
        if (o instanceof PaginatedResult)
        {
            PaginatedResult<T> other = (PaginatedResult<T>) o;

            if (other.page == this.page &&
                    other.pageSize == this.pageSize)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
}
