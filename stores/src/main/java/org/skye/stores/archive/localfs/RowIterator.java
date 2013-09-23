package org.skye.stores.archive.localfs;

import org.eobjects.metamodel.data.Row;

import java.util.Iterator;

/**
 * A little helper to go from the metamodels row implementation
 * over to our {@link org.skye.core.structured.Row}
 */
public class RowIterator implements Iterator<org.skye.core.structured.Row> {

    private final Iterator<Row> iterator;

    public RowIterator(Iterator<Row> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    private org.skye.core.structured.Row convert(Row row) {
        org.skye.core.structured.Row newRow = new org.skye.core.structured.Row();
        newRow.setValues(row.getValues());
        return newRow;
    }

    @Override
    public org.skye.core.structured.Row next() {
        return convert(iterator.next());
    }

    @Override
    public void remove() {
        iterator.remove();
    }
}
