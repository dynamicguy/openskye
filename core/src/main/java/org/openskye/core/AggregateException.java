package org.openskye.core;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Exception which reports that multiple errors have occurred in an iterative operation
 * after that operation has finished.  This allows an operation to continue processing with
 * the next record, as in systems without a transaction that can be rolled back on error.
 */
public class AggregateException extends RuntimeException {
    @Getter
    private int numberOfFailures = 0;

    @Getter
    private Exception firstException = null;

    public AggregateException() {
        super();
    }

    public AggregateException(String s) {
        super(s);
    }

    public void add(Exception ex) {
        if (isEmpty()) {
            firstException = ex;
        }

        numberOfFailures++;
    }

    public long count() {
        return numberOfFailures;
    }

    public boolean isEmpty() {
        return (numberOfFailures == 0);
    }
}
