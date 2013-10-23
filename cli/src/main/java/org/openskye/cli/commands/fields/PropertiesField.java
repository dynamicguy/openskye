package org.openskye.cli.commands.fields;


import java.util.HashMap;
import java.util.Map;

public class PropertiesField extends Field {

    private final Map<String, String> properties;
    private final String name;

    public PropertiesField(String name) {
        this.properties = new HashMap<>();
        this.name = name;
    }

    public void addProperty(String property, String value){
        properties.put(property, value);
    }

    public Map<String, String> getProperties(){
        return this.properties;
    }

    @Override
    public String getName() {
        return name;
    }
}
