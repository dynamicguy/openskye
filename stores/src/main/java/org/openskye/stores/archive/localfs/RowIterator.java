package org.openskye.stores.archive.localfs;

import org.eobjects.metamodel.data.Row;

import java.util.Iterator;

/**
 * A little helper to go from the metamodels row implementation
 * over to our {@link org.openskye.core.structured.Row}
 */
public class RowIterator implements Iterator<org.openskye.core.structured.Row> {

    private final Iterator<Row> iterator;

    public RowIterator(Iterator<Row> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    private org.openskye.core.structured.Row convert(Row row) {
        org.openskye.core.structured.Row newRow = new org.openskye.core.structured.Row();
        newRow.setValues(row.getValues());
        return newRow;
    }

    @Override
    public org.openskye.core.structured.Row next() {
        return convert(iterator.next());
    }

    @Override
    public void remove() {
        iterator.remove();
    }
}
