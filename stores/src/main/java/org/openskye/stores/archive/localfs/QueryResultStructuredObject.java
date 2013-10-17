package org.openskye.stores.archive.localfs;

import org.eobjects.metamodel.data.DataSet;
import org.eobjects.metamodel.query.SelectItem;
import org.openskye.core.StructuredObject;
import org.openskye.core.structured.ColumnMetadata;
import org.openskye.core.structured.Row;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A {@link StructuredObject} that represents the results of a call to a
 * {@link org.openskye.core.QueryableStore}
 */
public class QueryResultStructuredObject extends StructuredObject {
    private final DataSet dataset;

    public QueryResultStructuredObject(DataSet dataset) {
        this.dataset = dataset;
    }

    @Override
    public List<ColumnMetadata> getColumns() {
        List<ColumnMetadata> cols = new ArrayList<>();
        for (SelectItem item : dataset.getSelectItems()) {
            ColumnMetadata colMeta = new ColumnMetadata();
            colMeta.setName(item.getColumn().getName());
            colMeta.setNativeType(item.getColumn().getNativeType());
            colMeta.setRemarks(item.getColumn().getRemarks());
            //colMeta.setSize(item.getColumn().getColumnSize());
            cols.add(colMeta);
        }
        return cols;
    }

    @Override
    public Iterator<Row> getRows() {
        return new RowIterator(dataset.iterator());
    }
}
