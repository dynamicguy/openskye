package org.skye.util;

import lombok.Data;

import java.util.List;

/**
 * A utility to handle paginated results
 */
@Data
public class PaginatedResult<T> {

    private long page;
    private long pageSize;
    private long totalResults;
    private List<T> results;

    // TODO This isn't great since we are doing some messy stuff in
    // here
    public PaginatedResult<T> paginate(List<T> list) {
        results = list;
        return this;
    }
}
