package org.skye.stores.information.jdbc;

import lombok.Data;
import org.eobjects.metamodel.DataContext;
import org.eobjects.metamodel.schema.Column;
import org.eobjects.metamodel.schema.Table;
import org.skye.core.StructuredObject;
import org.skye.core.structured.ColumnMetadata;
import org.skye.core.structured.Row;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The implementation of a {@link StructuredObject}
 */
@Data
public class JDBCStructuredObject extends StructuredObject {

    private final DataContext dataContext;
    private Table table;

    public JDBCStructuredObject(DataContext dataContext, Table table) {
        this.table = table;
        this.dataContext = dataContext;
    }

    @Override
    public List<ColumnMetadata> getColumns() {
        List<ColumnMetadata> colMetas = new ArrayList<>();
        for (Column column : table.getColumns()) {
            ColumnMetadata colMeta = new ColumnMetadata();
            colMeta.setName(column.getName());
            colMeta.setSize(column.getColumnSize());
            colMeta.setNativeType(column.getNativeType());
            colMeta.setRemarks(column.getRemarks());
            colMetas.add(colMeta);
        }
        return colMetas;
    }

    @Override
    public Iterator<Row> getRows() {
        return new QueryIterator(dataContext.executeQuery(dataContext.query().from(table).selectAll().toQuery()).iterator());
    }

}
