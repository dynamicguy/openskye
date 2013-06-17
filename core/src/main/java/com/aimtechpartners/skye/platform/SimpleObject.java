package com.aimtechpartners.skye.platform;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A base abstract type for taggable metadata to use
 */
public abstract class SimpleObject {

    private String path;
    private List<String> tags = new ArrayList();
    private Map<String, String> metadata = new HashMap();
    private List<SimpleObject> related = new ArrayList();

    public List<SimpleObject> getRelated() {
        return related;
    }

    public void setRelated(List<SimpleObject> related) {
        this.related = related;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
