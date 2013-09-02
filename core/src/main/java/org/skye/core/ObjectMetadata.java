package org.skye.core;

import lombok.Data;
import org.joda.time.DateTime;
import org.skye.domain.DomainArchiveStore;
import org.skye.domain.DomainInformationStore;
import org.skye.domain.Project;

import java.util.*;

/**
 * A set of the metadata for an instance of an Object within Skye
 */
@Data
public class ObjectMetadata {

    private String id;
    private String path;
    // The taskId for the task that found/ingested the simple object
    private String taskId;
    private Set<Tag> tags = new HashSet<>();
    private Map<String, String> metadata = new HashMap<>();
    private boolean container;

    private DateTime lastModified = new DateTime();
    private DateTime created = new DateTime();
    private long size = 0;

    private String mimeType;
    private Project project;
    private DomainInformationStore informationStore;
    private DomainArchiveStore domainArchiveStore;
    private boolean ingested;
    private List<ArchiveContentBlock> archiveContentBlocks = new ArrayList<>();
}
