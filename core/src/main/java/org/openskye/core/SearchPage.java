package org.openskye.core;

public class SearchPage
{
    private int pageNumber;
    private int pageSize;

    public SearchPage(int pageNumber, int pageSize)
    {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public int getPageNumber()
    {
        return pageNumber;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public int getPageStart()
    {
        return (pageNumber - 1) * pageSize;
    }

    public int getPageEnd()
    {
        return (pageNumber * pageSize);
    }
}
