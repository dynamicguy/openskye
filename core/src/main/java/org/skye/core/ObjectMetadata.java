package org.skye.core;

import com.google.common.base.Optional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.skye.domain.Project;

import java.util.*;

/**
 * A set of the metadata for an instance of an Object within Skye
 */
@Data
@Slf4j
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
    private DateTime ingested = new DateTime();
    private DateTime lastModified = new DateTime();
    private Project project;
    private long originalSize = 0;
    private long archiveSize = 0;
    private String mimeType;
    private String checksum;
    private String informationStoreId;
    private List<ArchiveContentBlock> archiveContentBlocks = new ArrayList<>();

    /**
     * A mechanism to find it there is an {@link ArchiveContentBlock} for a given
     * {@link org.skye.domain.ArchiveStoreDefinition} by id
     *
     * @param archiveStoreDefinitionId The id of the {@link org.skye.domain.ArchiveStoreDefinition}
     * @return return an instance of the {@link ArchiveContentBlock} if one is found for the archive store
     */
    public Optional<ArchiveContentBlock> getArchiveContentBlock(String archiveStoreDefinitionId) {
        log.debug("Resolving ACB for " + archiveStoreDefinitionId);
        for (ArchiveContentBlock acb : this.getArchiveContentBlocks()) {
            if (acb.getArchiveStoreDefinitionId().equals(archiveStoreDefinitionId)) {
                log.debug("Found ACB " + acb);
                return Optional.of(acb);
            }
        }
        log.debug("No ACB found for " + archiveStoreDefinitionId);
        return Optional.absent();
    }
}
