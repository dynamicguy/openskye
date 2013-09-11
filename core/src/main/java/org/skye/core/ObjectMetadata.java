package org.skye.core;

import lombok.Data;
import org.joda.time.DateTime;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.domain.InformationStoreDefinition;
import org.skye.domain.Project;

import java.util.*;

/**
 * A set of the metadata for an instance of an Object within Skye
 */
@Data
public class ObjectMetadata {

    private String id = UUID.randomUUID().toString();
    private String path;
    // The taskId for the task that found/ingested the simple object
    private String taskId;
    private Set<Tag> tags = new HashSet<>();
    private Map<String, String> metadata = new HashMap<>();
    private boolean container;
    private DateTime created = new DateTime();
    private long originalSize = 0;
    private long archiveSize = 0;
    private String mimeType;
    private String checksum;
    private DateTime ingested;
    private Project project;
    private InformationStoreDefinition informationStore;
    private ArchiveStoreDefinition archiveStoreDefinition;
    private List<ArchiveContentBlock> archiveContentBlocks = new ArrayList<>();
}
