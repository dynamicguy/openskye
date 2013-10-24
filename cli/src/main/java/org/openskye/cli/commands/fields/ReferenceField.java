package org.openskye.cli.commands.fields;

import com.google.common.base.CaseFormat;
import lombok.Data;
import org.apache.commons.lang.WordUtils;

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

    //Constructor for ReferenceFields with specific names

    public ReferenceField(String name, Class clazz) {
        this.name = name;
        this.resource = getResourceString(clazz);
        this.id = "id";
        this.value = "name";
        this.clazz = clazz;
    }

    public ReferenceField(Class clazz){
        this.name = WordUtils.uncapitalize(clazz.getSimpleName());
        this.clazz = clazz;
        this.id = "id";
        this.value = "name";
        this.resource = getResourceString(clazz);
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
