package org.openskye.cli.commands.fields;

import com.google.common.base.CaseFormat;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang.WordUtils;

/**
 * The representation of a {@link org.openskye.cli.commands.fields.ReferenceField}
 */
@Data
public class ReferenceField extends Field {
    @Getter
    protected final String name;
    protected final String resource;
    protected final String value;
    protected final String id;
    protected final Class clazz;

    //Constructor for ReferenceFields with specific names

    public ReferenceField(String name, Class clazz, boolean optional) {
        this.name = name;
        this.resource = getResourceString(clazz);
        this.id = "id";
        this.value = "name";
        this.clazz = clazz;
        this.isOptional=optional;
    }

    public ReferenceField(Class clazz, boolean optional){
        this.name = WordUtils.uncapitalize(clazz.getSimpleName());
        this.clazz = clazz;
        this.id = "id";
        this.value = "name";
        this.resource = getResourceString(clazz);
        this.isOptional=optional;
    }

    public ReferenceField() {
        name = null;
        resource = null;
        value = null;
        id = null;
        clazz = Object.class;
    }

    public String getResourceString(Class clazz){
        String resourceString = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, clazz.getSimpleName());
        if(resourceString.endsWith("s")){
            return resourceString;
        } else {
            return resourceString+"s";
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
