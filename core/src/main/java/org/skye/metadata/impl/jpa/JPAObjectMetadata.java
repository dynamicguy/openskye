package org.skye.metadata.impl.jpa;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;
import org.skye.core.ArchiveContentBlock;
import org.skye.core.ObjectMetadata;
import org.skye.core.SkyeException;
import org.skye.core.Tag;
import org.skye.domain.Project;
import org.skye.stores.StoreRegistry;

import javax.persistence.*;
import java.util.*;

/**
 * The JPA Entity which will represent {@link org.skye.core.ObjectMetadata} for
 * persistence purposes.  It also contains methods which allow the Entity to
 * be copied from a non persisted {@link ObjectMetadata}, as well as a method
 * which copies this JPAObjectMetadata to an {@link ObjectMetadata}.
 * <p/>
 * Note that for this operation to be successful, one must either set the
 * static storeRegistry field on the {@link JPAArchiveContentBlock} manually
 * or the GuiceBerry module should use requestStaticInjection on
 * {@link JPAArchiveContentBlock} in order to perform these translations.
 */
@Entity
@Table(name = "OBJECT_METADATA")
@Data
public class JPAObjectMetadata {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
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
    private DateTime created = new DateTime();
    private DateTime ingested = new DateTime();
    @ManyToOne
    private Project project = null;
    private String informationStoreDefinitionId = "";
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
        this.implementation = objectMetadata.getImplementation();
        this.taskId = objectMetadata.getTaskId();
        this.metadata = objectMetadata.getMetadata();
        this.container = objectMetadata.isContainer();
        this.created = objectMetadata.getCreated();
        this.ingested = objectMetadata.getIngested();
        this.project = objectMetadata.getProject();
        this.informationStoreDefinitionId = objectMetadata.getInformationStoreId();

        this.tags = new HashSet<>();

        for (Tag tag : objectMetadata.getTags()) {
            JPATag jpaTag = new JPATag(tag);
            this.tags.add(jpaTag);
        }

        for (ArchiveContentBlock acb : objectMetadata.getArchiveContentBlocks()) {
            JPAArchiveContentBlock jpaACB = new JPAArchiveContentBlock();
            archiveContentBlocks.add(jpaACB);
        }
    }

    /**
     * Creates an {@link ObjectMetadata} copy of the JPA Entity version.
     * <p/>
     * Note that the {@link JPAArchiveContentBlock} must have a valid
     * {@link StoreRegistry} set statically in order for this translation
     * to work.  Either set the static field, storeRegistry, directly, or use
     * GuiceBerry's requestStaticInjection method to ensure that the
     * storeRegistry is initialized.
     *
     * @return An {@link ObjectMetadata} copy of this Entity.
     * @throws SkyeException See the {@link JPAArchiveContentBlock} method,
     *                       ToArchiveContentBlock() for more information on possible exceptions.
     */
    public ObjectMetadata ToObjectMetadata() throws SkyeException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        Set<Tag> tags = new HashSet<>();
        List<ArchiveContentBlock> blocks = new ArrayList<>();

        objectMetadata.setId(this.id);
        objectMetadata.setImplementation(this.implementation);
        objectMetadata.setTaskId(this.taskId);
        objectMetadata.setMetadata(this.metadata);
        objectMetadata.setContainer(this.container);
        objectMetadata.setCreated(this.created);
        objectMetadata.setIngested(this.ingested);
        objectMetadata.setProject(this.project);
        objectMetadata.setInformationStoreId(this.informationStoreDefinitionId);

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
