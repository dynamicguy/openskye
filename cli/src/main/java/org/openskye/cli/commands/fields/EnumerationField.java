package org.openskye.cli.commands.fields;

import scala.actors.threadpool.Arrays;

import java.util.EnumSet;
import java.util.List;

/**
 * The representation of a {@link org.openskye.cli.commands.fields.EnumerationField}
 */
public class EnumerationField extends Field{

    private final EnumSet<? extends Enum> anEnum;
    private final String name;
    private final Class clazz;

    public EnumerationField(String name, Class newEnum, boolean optional) {
        this.name = name;
        this.clazz=newEnum;
        this.anEnum=EnumSet.allOf(newEnum);
        this.isOptional=optional;
    }

    public List<? extends Enum> getAllEnumOptions(){

        return Arrays.asList(anEnum.toArray());
    }

    @Override
    public String getName() {
        return name;
    }

    public Class getClazz(){
        return clazz;
    }


}
