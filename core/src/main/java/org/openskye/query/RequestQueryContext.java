package org.openskye.query;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * A basic value object for a few useful things that we probably want to pick up
 * on a filter in the API and then make accessible from everywhere
 */
@Data
public class RequestQueryContext {

    private int page = 1;
    private int pageSize = 10;
    private String sort = null;
    private SortDirection sortDir = SortDirection.ASC;
    private Map<String, String> filter = new HashMap<>();

    public void setPage(int page) {
        this.page = Math.max(page, 1);
    }

    public int getOffset() {
        return Math.max((page - 1) * pageSize, 1);
    }
}
