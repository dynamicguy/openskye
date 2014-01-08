package org.openskye.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openskye.core.structured.ColumnMetadata;
import org.openskye.core.structured.Row;

import java.util.Iterator;
import java.util.List;

/**
 * A <code>StructuredObject</code> is the metadata representing an object within the enterprise which is structured
 * <p/>
 * These structure can be used to model structured objects such as tables, or structured files
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class StructuredObject extends SimpleObject {

    public abstract List<ColumnMetadata> getColumns();

    /**
     * Returns an iterator to the rows in the database
     *
     * @return an iterator to the rows in the database
     */
    public abstract Iterator<Row> getRows();

}
