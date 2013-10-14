package org.skye.cli.commands.fields;

import lombok.Data;

/**
 * The representation of a {@link org.skye.cli.commands.fields.ReferenceField}
 */
@Data
public class ReferenceField extends Field {

    private final String name;
    private final String resource;
    private final String description;
    private final String id;

    public ReferenceField(String name, String resource) {
        this.name = name;
        this.resource = resource;
        this.id = "id";
        this.description = "description";
    }

    public ReferenceField(String name, String resource, String id, String description) {
        this.name = name;
        this.resource = resource;
        this.id = id;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }
}
