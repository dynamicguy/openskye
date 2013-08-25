package org.skye.stores.information;

import lombok.Data;
import org.eobjects.metamodel.schema.Table;
import org.skye.core.StructuredObject;
import org.skye.core.structured.Row;

import java.util.Iterator;

/**
 * The implementation of a {@link StructuredObject}
 */
@Data
public class JDBCStructuredObject extends StructuredObject {

    private Table table;

    @Override
    public Iterator<Row> getRows() {
        return null;
    }

}
