package org.skye.cli.commands.fields;

import lombok.Data;

/**
 * The representation of a {@link org.skye.cli.commands.fields.ReferenceField}
 */
@Data
public class ReferenceField extends Field {

    private final String name;
    private final String resource;
    private final String value;
    private final String id;
    private final Class clazz;

    public ReferenceField(String name, String resource, Class clazz) {
        this.name = name;
        this.resource = resource;
        this.id = "id";
        this.value = "name";
        this.clazz = clazz;
    }

    public ReferenceField(String name, String resource, Class clazz, String id, String value) {
        this.name = name;
        this.resource = resource;
        this.clazz = clazz;
        this.id = id;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }
}
