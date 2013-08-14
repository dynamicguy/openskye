package org.skye.core.structured;

import lombok.Data;

/**
 * Representation of the metadata for a column in a {@link org.skye.core.StructuredObject}
 */
@Data
public class ColumnMetadata {

    private String name;
    private String nativeType;
    private int size;
    private String remarks;
}
