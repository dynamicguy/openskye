package org.openskye.cli.util;

import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;
import org.h2.util.StringUtils;
import org.openskye.core.SkyeException;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple representation of a column
 */
@Data
public class TableColumn {

    private final String name;
    private List<String> values = new ArrayList<>();
    private int maxLength;
    private Object columnHeader;

    public TableColumn(String name) {
        this.name = name;
        maxLength = name.length();
    }

    public void addValue(String value) {
        values.add(value);
        int length = getLength(value);
        if (maxLength < length)
            maxLength = length;
    }

    private int getLength(String value) {
        if (value == null)
            // We return 4 so we can have space for
            // null
            return 4;
        else
            return value.length();
    }

    public void parseObject(Object obj) {
        try {
            addValue(BeanUtils.getProperty(obj, name));
        } catch (Exception e) {
            throw new SkyeException("Unable to access field " + name + " on " + obj, e);
        }
    }

    public Object getRowValue(int i) {
        if (values.get(i) != null)
            return StringUtils.pad(values.get(i), getMaxLength(), " ", true);
        else
            return StringUtils.pad("null", getMaxLength(), " ", true);
    }

    public String getColumnHeader() {
        return StringUtils.pad(name, getMaxLength(), " ", true);
    }
}
