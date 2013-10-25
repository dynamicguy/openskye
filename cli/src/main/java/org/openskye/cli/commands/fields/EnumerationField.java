package org.openskye.cli.commands.fields;

import java.util.Arrays;
import java.util.List;

/**
 * The representation of a {@link org.openskye.cli.commands.fields.EnumerationField}
 */
public class EnumerationField extends Field{

    private final Class<?> anEnum;
    private final String name;

    public EnumerationField(String name, Class newEnum) {
        this.name = name;
        this.anEnum=newEnum;
    }

    public List<?> getAllEnumOptions(){
        List<?> results = Arrays.asList(anEnum.getEnumConstants());
        return results;
    }

    public Object getEnum(int select){
        List<?> enums = getAllEnumOptions();
        return enums.get(select);
    }

    @Override
    public String getName() {
        return name;
    }
}
