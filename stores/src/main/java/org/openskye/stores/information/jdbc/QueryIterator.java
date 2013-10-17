package org.openskye.stores.information.jdbc;

import org.openskye.core.structured.Row;

import java.util.Iterator;

/**
 * A helper for iterating the results of a query
 */
public class QueryIterator implements Iterator<Row> {

    private final Iterator<org.eobjects.metamodel.data.Row> interalIterator;

    public QueryIterator(Iterator<org.eobjects.metamodel.data.Row> internalIterator) {
        this.interalIterator = internalIterator;
    }

    @Override
    public boolean hasNext() {
        return interalIterator.hasNext();
    }

    @Override
    public Row next() {
        org.eobjects.metamodel.data.Row internalRow = interalIterator.next();
        Row row = new Row();
        row.setValues(internalRow.getValues());
        return row;
    }

    @Override
    public void remove() {
        interalIterator.remove();
    }
}
