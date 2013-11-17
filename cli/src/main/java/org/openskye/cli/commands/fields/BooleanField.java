package org.openskye.cli.commands.fields;

/**
 * The representation of a {@link org.openskye.cli.commands.fields.BooleanField}
 */
public class BooleanField extends Field {

    private final String name;

    public BooleanField(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}

