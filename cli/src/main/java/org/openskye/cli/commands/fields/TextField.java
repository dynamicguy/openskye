package org.openskye.cli.commands.fields;

/**
 * The representation of a {@link org.openskye.cli.commands.fields.TextField}
 */
public class TextField extends Field {

    private final String name;

    public TextField(String name, boolean optional) {
        this.name = name;
        this.isOptional=optional;
    }

    @Override
    public String getName() {
        return name;
    }
}
