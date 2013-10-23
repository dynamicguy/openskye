package org.openskye.cli.commands.fields;

/**
 * The representation of a {@link org.openskye.cli.commands.fields.EnumerationField}
 */
public class EnumerationField extends Field{

    private final Class<?> anEnum;
    private final String name;

    public EnumerationField(String name, Class<?> newEnum) {
        this.name = name;
        this.anEnum=newEnum;
    }

    public String[] getAllEnumOptions(){
        return (String[])anEnum.getEnumConstants();
    }

    public String getEnum(int select){
        String[] enums = getAllEnumOptions();
        return enums[select];
    }

    @Override
    public String getName() {
        return name;
    }
}
