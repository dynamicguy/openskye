package org.skye.core;

import com.google.common.base.Optional;
import lombok.Data;
import org.joda.time.DateTime;
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
    // Identify the implementation of the simple object
    private String implementation;
    // The taskId for the task that found/ingested the simple object
    private String taskId;
    private Set<Tag> tags = new HashSet<>();
    private Map<String, String> metadata = new HashMap<>();
    private boolean container;
    private DateTime created = new DateTime();
    private DateTime ingested;
    private Project project;
    private InformationStoreDefinition informationStore;
    private List<ArchiveContentBlock> archiveContentBlocks = new ArrayList<>();

    public Optional<ArchiveContentBlock> getArchiveContentBlock(ArchiveStore archiveStore) {
        for (ArchiveContentBlock acb : this.getArchiveContentBlocks()) {
            if (acb.getArchiveStore().getUrl().equals(archiveStore.getUrl())) {
                return Optional.of(acb);
            }
        }
        return Optional.absent();
    }
}
