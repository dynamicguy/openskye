package org.openskye.cli.commands.fields;

import com.google.common.base.CaseFormat;
import lombok.Data;

/**
 * The representation of a {@link org.openskye.cli.commands.fields.ReferenceField}
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

    public ReferenceField(Class clazz){
        this.name = clazz.getSimpleName();
        this.clazz = clazz;
        this.id = "id";
        this.value = "name";
        this.resource = getResourceString(clazz);
    }

    public String getResourceString(Class clazz){
        String resourceString = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name);
        return resourceString+"s";
    }

    @Override
    public String getName() {
        return name;
    }
}
