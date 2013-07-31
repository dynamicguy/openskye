package org.skye.core;

/**
 * Represents a named tag that can be associated with {@link SimpleObject}
 */
public class Tag {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Tag) ? ((Tag) obj).getName().equals(getName()) : false;
    }
}
