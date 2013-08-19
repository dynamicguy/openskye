package org.skye.core;

import lombok.Data;

import java.util.*;

/**
 * A base abstract type to represent a simple object within the enterprise
 */
@Data
public abstract class SimpleObject {

    private String id;
    private String path;
    private Set<Tag> tags = new HashSet<>();
    private Map<String, String> metadata = new HashMap<>();
    private boolean container;
    private String mimeType;
    private InformationStore informationStore;
    private List<ArchiveContentBlock> archiveContentBlocks = new ArrayList<>();
}
