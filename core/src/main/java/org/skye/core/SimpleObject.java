package org.skye.core;

import lombok.Data;
import org.skye.domain.DomainArchiveStore;

import java.util.*;

/**
 * A base abstract type to represent a simple object within the enterprise
 */
@Data
public abstract class SimpleObject {

    private String id;
    private String path;
    // The taskId for the task that found/ingested the simple object
    private String taskId;
    private Set<Tag> tags = new HashSet<>();
    private Map<String, String> metadata = new HashMap<>();
    private boolean container;
    private String mimeType;
    private InformationStore informationStore;
    private DomainArchiveStore domainArchiveStore;
    private boolean ingested;
    private List<ArchiveContentBlock> archiveContentBlocks = new ArrayList<>();

}
