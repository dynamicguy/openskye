package org.skye.cli.commands.fields;

import java.util.ArrayList;
import java.util.List;

/**
 * A little helper class for building lists of {@link Field}
 */
public class FieldBuilder {

    private List<Field> fields = new ArrayList<>();

    public static FieldBuilder start() {
        return new FieldBuilder();
    }

    public FieldBuilder add(Field field) {
        fields.add(field);
        return this;
    }

    public List<Field> build() {
        return fields;
    }
}
