package org.openskye.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Optional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.openskye.domain.ArchiveStoreInstance;
import org.openskye.domain.Project;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    @Column(unique = true, length = 36)
    @NotNull
    private String id = UUID.randomUUID().toString();
    @Size(min = 1, max = 1024)
    @Column(length = 1024)
    private String path;
    private String implementation;
    private String taskId;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "OBJECT_METADATA_TAGS",
            joinColumns = @JoinColumn(name = "OBJECT_METADATA_ID")
    )
    @Column(name = "tag")
    private Set<Tag> tags = new HashSet<>();
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "OBJECT_METADATA_DATA",
            joinColumns = @JoinColumn(name = "OBJECT_METADATA_ID")
    )
    @Column(name = "metadata")
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
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "OBJECT_METADATA_ARCHIVE_CONTENT_BLOCK",
            joinColumns = {@JoinColumn(name = "OBJECT_METADATA_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "ARCHIVE_CONTENT_BLOCK_ID", referencedColumnName = "ID")})
    private List<ArchiveContentBlock> archiveContentBlocks = new ArrayList<>();

    /**
     * A mechanism to find it there is an {@link ArchiveContentBlock} for a given
     * {@link org.openskye.domain.ArchiveStoreDefinition} by id
     *
     * @param archiveStoreInstance The {@link org.openskye.domain.ArchiveStoreInstance}
     * @return return an instance of the {@link ArchiveContentBlock} if one is found for the archive store
     */
    public Optional<ArchiveContentBlock> getArchiveContentBlock(ArchiveStoreInstance archiveStoreInstance) {
        log.debug("Resolving ACB for " + archiveStoreInstance);
        for (ArchiveContentBlock acb : this.getArchiveContentBlocks()) {
            if (acb.getArchiveStoreInstance().equals(archiveStoreInstance)) {
                log.debug("Found ACB " + acb);
                return Optional.of(acb);
            }
        }
        log.debug("No ACB found for " + archiveStoreInstance);
        return Optional.absent();
    }
}
