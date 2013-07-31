package org.skye.util;

/**
 * A simple representation of a page
 */
public class Page {

    private long pageNumber;
    private long pageSize;

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(long pageNumber) {
        this.pageNumber = pageNumber;
    }

}
