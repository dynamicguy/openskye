package org.skye.cli.commands.fields;

/**
 * The representation of a {@link org.skye.cli.commands.fields.ReferenceField}
 */
public class ReferenceField extends Field {

    private final String name;

    public ReferenceField(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
