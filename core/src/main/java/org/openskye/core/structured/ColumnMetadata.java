package org.openskye.core.structured;

import lombok.Data;

/**
 * Representation of the metadata for a column in a {@link org.openskye.core.StructuredObject}
 */
@Data
public class ColumnMetadata {

    private String name;
    private String nativeType;
    private int size;
    private String remarks;

}
