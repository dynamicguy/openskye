package com.infobelt.skye.platform;

import java.util.UUID;

/**
 * Represents a named tag that ca be associated with {@link SimpleObject}
 */
public class Tag {

    private String name;
    private UUID id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
