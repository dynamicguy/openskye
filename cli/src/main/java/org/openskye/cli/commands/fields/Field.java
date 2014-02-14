package org.openskye.cli.commands.fields;

import lombok.Data;
import lombok.Getter;

/**
 * The representation of a {@link Field}
 */
@Data
public abstract class Field {
    @Getter
    public boolean isOptional = false;

    /**
     * Get the name of this field
     * @return the field name
     */
    public abstract String getName();

}
