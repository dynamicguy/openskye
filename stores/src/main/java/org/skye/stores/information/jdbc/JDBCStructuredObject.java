package org.skye.stores.information.jdbc;

import lombok.Data;
import org.eobjects.metamodel.DataContext;
import org.eobjects.metamodel.schema.Table;
import org.skye.core.StructuredObject;
import org.skye.core.structured.Row;

import java.util.Iterator;

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
    public Iterator<Row> getRows() {
        return new QueryIterator(dataContext.executeQuery(dataContext.query().from(table).selectAll().toQuery()).iterator());
    }

}
