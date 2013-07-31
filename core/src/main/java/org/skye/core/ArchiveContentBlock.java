package org.skye.core;

/**
 * A block of content that relates to a {@link SimpleObject}
 */
public class ArchiveContentBlock {

    private String id;
    private SimpleObject simpleObject;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SimpleObject getSimpleObject() {
        return simpleObject;
    }

    public void setSimpleObject(SimpleObject simpleObject) {
        this.simpleObject = simpleObject;
    }
}
