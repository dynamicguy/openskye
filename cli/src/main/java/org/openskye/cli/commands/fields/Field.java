package org.openskye.cli.commands.fields;

import lombok.Data;

/**
 * The representation of a {@link Field}
 */
@Data
public abstract class Field {

    /**
     * Get the name of this field
     * @return the field name
     */
    public abstract String getName();

}
