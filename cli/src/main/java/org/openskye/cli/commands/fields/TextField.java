package org.openskye.cli.commands.fields;

/**
 * The representation of a {@link org.openskye.cli.commands.fields.TextField}
 */
public class TextField extends Field {

    private final String name;

    public TextField(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
