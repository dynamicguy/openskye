package org.openskye.metadata.impl.jpa;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.SkyeException;
import org.openskye.core.Tag;
import org.openskye.domain.Project;

import javax.persistence.*;
import java.util.*;

/**
 * The JPA Entity which will represent {@link org.openskye.core.ObjectMetadata} for
 * persistence purposes.  It also contains methods which allow the Entity to
 * be copied from a non persisted {@link ObjectMetadata}, as well as a method
 * which copies this JPAObjectMetadata to an {@link ObjectMetadata}.
 */
@Entity
@Table(name = "OBJECT_METADATA")
@Data
@EqualsAndHashCode(of = "id")
public class JPAObjectMetadata {
    @Id
    @Column(unique = true)
    private String id;
    private String path = "";
    private String implementation = "";
    private String taskId = "";
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<JPATag> tags = new HashSet<>();
    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, String> metadata = new HashMap<>();
    private boolean container = false;
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime created = new DateTime();
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime ingested = new DateTime();
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastModified = new DateTime();
    @ManyToOne
    private Project project = null;
    private long originalSize = 0;
    private long archiveSize = 0;
    private String mimeType = "";
    private String checksum = "";
    private String informationStoreId = "";
    @ElementCollection(fetch = FetchType.EAGER)
    private List<JPAArchiveContentBlock> archiveContentBlocks = new ArrayList<>();

    /**
     * The default constructor for the JPAObjectMetadata class.
     */
    public JPAObjectMetadata() {
        // nothing to do
    }

    /**
     * Creates the JPAObjectMetadata as a copy of an existing
     * {@link ObjectMetadata}.
     *
     * @param objectMetadata The {@link ObjectMetadata} to be copied.
     */
    public JPAObjectMetadata(ObjectMetadata objectMetadata) {
        this.id = objectMetadata.getId();
        this.path = objectMetadata.getPath();
        this.implementation = objectMetadata.getImplementation();
        this.taskId = objectMetadata.getTaskId();
        this.metadata = objectMetadata.getMetadata();
        this.container = objectMetadata.isContainer();
        this.created = objectMetadata.getCreated();
        this.ingested = objectMetadata.getIngested();
        this.lastModified = objectMetadata.getLastModified();
        this.project = objectMetadata.getProject();
        this.originalSize = objectMetadata.getOriginalSize();
        this.archiveSize = objectMetadata.getArchiveSize();
        this.mimeType = objectMetadata.getMimeType();
        this.checksum = objectMetadata.getChecksum();
        this.informationStoreId = objectMetadata.getInformationStoreId();

        this.tags = new HashSet<>();

        for (Tag tag : objectMetadata.getTags()) {
            JPATag jpaTag = new JPATag(tag);
            this.tags.add(jpaTag);
        }

        for (ArchiveContentBlock acb : objectMetadata.getArchiveContentBlocks()) {
            JPAArchiveContentBlock jpaACB = new JPAArchiveContentBlock(acb);
            archiveContentBlocks.add(jpaACB);
        }
    }

    /**
     * Creates an {@link ObjectMetadata} copy of the JPA Entity version.
     *
     * @return An {@link ObjectMetadata} copy of this Entity.
     * @throws SkyeException See the {@link JPAArchiveContentBlock} method,
     *                       ToArchiveContentBlock() for more information on possible exceptions.
     */
    public ObjectMetadata toObjectMetadata() {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        Set<Tag> tags = new HashSet<>();
        List<ArchiveContentBlock> blocks = new ArrayList<>();

        objectMetadata.setId(this.id);
        objectMetadata.setPath(this.path);
        objectMetadata.setImplementation(this.implementation);
        objectMetadata.setTaskId(this.taskId);
        objectMetadata.setMetadata(this.metadata);
        objectMetadata.setContainer(this.container);
        objectMetadata.setCreated(this.created);
        objectMetadata.setIngested(this.ingested);
        objectMetadata.setLastModified(this.lastModified);
        objectMetadata.setProject(this.project);
        objectMetadata.setOriginalSize(this.originalSize);
        objectMetadata.setArchiveSize(this.archiveSize);
        objectMetadata.setMimeType(this.mimeType);
        objectMetadata.setChecksum(this.checksum);
        objectMetadata.setInformationStoreId(this.informationStoreId);

        for (JPATag jpaTag : this.tags) {
            tags.add(jpaTag.ToTag());
        }

        for (JPAArchiveContentBlock jpaBlock : this.archiveContentBlocks) {
            blocks.add(jpaBlock.toArchiveContentBlock());
        }

        objectMetadata.setTags(tags);
        objectMetadata.setArchiveContentBlocks(blocks);

        return objectMetadata;
    }
}
