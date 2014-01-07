package org.openskye.cli.commands.fields;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * The representation of a {@link org.openskye.cli.commands.fields.EnumerationField}
 */
public class EnumerationField extends Field{

    private final EnumSet<? extends Enum> anEnum;
    private final String name;

    public EnumerationField(String name, Class newEnum) {
        this.name = name;
        this.anEnum=EnumSet.allOf(newEnum);
    }

    public List<String> getAllEnumOptions(){
        List<String> enumOptions = new ArrayList<>(this.anEnum.size());
        for(Enum e : anEnum){
            enumOptions.add(e.toString());
        }
        return enumOptions;
    }

    @Override
    public String getName() {
        return name;
    }
}
