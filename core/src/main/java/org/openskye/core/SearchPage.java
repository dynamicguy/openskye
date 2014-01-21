package org.openskye.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class SearchPage
{
    @Getter
    @Setter
    private long pageNumber;

    @Getter
    @Setter
    private long pageSize;

    public SearchPage()
    {
        this.pageNumber = 0;
        this.pageSize = 0;
    }

    public SearchPage(long pageNumber, long pageSize)
    {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public long getPageStart()
    {
        return (pageNumber - 1) * pageSize;
    }

    public long getPageEnd()
    {
        return (pageNumber * pageSize);
    }
}
