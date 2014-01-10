package org.openskye.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Optional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.openskye.domain.Project;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * A set of the metadata for an instance of an Object within Skye
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
@Entity
@Table(name = "OBJECT_METADATA")
@EqualsAndHashCode(of = "id")
public class ObjectMetadata {

    @Id
    @Column(unique = true)
    @NotNull
    private String id = UUID.randomUUID().toString();
    private String path;
    private String implementation;
    private String taskId;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="OBJECT_METADATA_TAGS",
            joinColumns=@JoinColumn(name="OBJECT_METADATA_ID")
    )
    @Column(name="tag")
    private Set<Tag> tags = new HashSet<>();
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="OBJECT_METADATA_DATA",
            joinColumns=@JoinColumn(name="OBJECT_METADATA_ID")
    )
    @Column(name="metadata")
    private Map<String, String> metadata = new HashMap<>();
    private boolean container = false;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime created = new DateTime();
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime ingested = new DateTime();
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastModified = new DateTime();
    @ManyToOne(fetch = FetchType.EAGER)
    private Project project = null;
    private long originalSize;
    private long archiveSize;
    private String mimeType;
    private String checksum;
    private String informationStoreId;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="ARCHIVE_CONTENT_BLOCKS",
            joinColumns=@JoinColumn(name="OBJECT_METADATA_ID")
    )
    @Column(name="archiveContentBlock")
    private List<ArchiveContentBlock> archiveContentBlocks = new ArrayList<>();

    /**
     * A mechanism to find it there is an {@link ArchiveContentBlock} for a given
     * {@link org.openskye.domain.ArchiveStoreDefinition} by id
     *
     * @param archiveStoreInstanceId The id of the {@link org.openskye.domain.ArchiveStoreInstance}
     * @return return an instance of the {@link ArchiveContentBlock} if one is found for the archive store
     */
    public Optional<ArchiveContentBlock> getArchiveContentBlock(String archiveStoreInstanceId) {
        log.debug("Resolving ACB for " + archiveStoreInstanceId);
        for (ArchiveContentBlock acb : this.getArchiveContentBlocks()) {
            if (acb.getArchiveStoreInstanceId() != null && acb.getArchiveStoreInstanceId().equals(archiveStoreInstanceId)) {
                log.debug("Found ACB " + acb);
                return Optional.of(acb);
            }
        }
        log.debug("No ACB found for " + archiveStoreInstanceId);
        return Optional.absent();
    }
}
