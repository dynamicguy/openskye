package org.skye.cli.commands.fields;

/**
 * The representation of a {@link org.skye.cli.commands.fields.TextField}
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
