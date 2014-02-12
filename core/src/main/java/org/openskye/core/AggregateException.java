package org.openskye.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Exception which reports that multiple errors have occurred in an iterative operation
 * after that operation has finished.  This allows an operation to continue processing with
 * the next record, as in systems without a transaction that can be rolled back on error.
 */
public class AggregateException extends RuntimeException
{
    private List<Exception> innerExceptions = new ArrayList<>();

    public AggregateException()
    {
        super();
    }

    public AggregateException(String s)
    {
        super(s);
    }

    public AggregateException(String s, List<Exception> innerExceptions)
    {
        super(s);

        this.innerExceptions = innerExceptions;
    }

    public List<Exception> getInnerExceptions()
    {
        return innerExceptions;
    }

    public void setInnerExceptions(List<Exception> innerExceptions)
    {
        this.innerExceptions = innerExceptions;
    }

    public void add(Exception ex)
    {
        innerExceptions.add(ex);
    }

    public void addAll(List<Exception> exceptionList)
    {
        innerExceptions.addAll(exceptionList);
    }

    public long count()
    {
        return innerExceptions.size();
    }

    public boolean isEmpty()
    {
        return innerExceptions.isEmpty();
    }

    public Iterator<Exception> iterator()
    {
        return innerExceptions.iterator();
    }
}
