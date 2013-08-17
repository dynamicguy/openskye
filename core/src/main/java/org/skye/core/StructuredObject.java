package org.skye.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.skye.core.structured.ColumnMetadata;

import java.util.List;

/**
 * A <code>StructuredObject</code> is the metadata representing an object within the enterprise which is structured
 * <p/>
 * These structure can be used to model structured objects such as tables, or structured files
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class StructuredObject extends SimpleObject {

    private List<ColumnMetadata> columns;
}
