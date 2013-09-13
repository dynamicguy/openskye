package org.skye.domain.dao;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility to handle paginated results
 */
@Data
public class PaginatedResult<T> {

    private long page;
    private long pageSize;
    private long totalResults;
    private List<T> results = new ArrayList<>();

    public PaginatedResult<T> paginate(List<T> list) {
        results = list;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PaginatedResult) {
            PaginatedResult<T> other = (PaginatedResult<T>) o;
            if (other.getPage() == this.page && other.getPageSize() == this.pageSize) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
