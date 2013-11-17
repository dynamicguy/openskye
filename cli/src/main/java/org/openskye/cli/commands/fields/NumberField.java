package org.openskye.cli.commands.fields;

/**
 * The representation of a {@link org.openskye.cli.commands.fields.NumberField}
 */
public class NumberField extends Field {

    private final String name;

    public NumberField(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
