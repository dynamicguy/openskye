package org.openskye.cli.commands.fields;

/**
 * The representation of a {@link org.openskye.cli.commands.fields.NumberField}
 */
public class NumberField extends Field {

    private final String name;

    public NumberField(String name, boolean optional) {
        this.name = name;
        this.isOptional=optional;
    }

    @Override
    public String getName() {
        return name;
    }
}
