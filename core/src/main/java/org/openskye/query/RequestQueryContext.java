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

    private int page = 0;
    private int pageSize = 20;
    private String sort = null;
    private SortDirection sortDir = SortDirection.ASC;
    private Map<String, String> filter = new HashMap<>();

    public int getOffset() {
        return page * pageSize;
    }
}
