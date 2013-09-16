package org.skye.metadata.impl.jpa;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import org.joda.time.DateTime;
import org.skye.core.ArchiveContentBlock;
import org.skye.core.ObjectMetadata;
import org.skye.core.Tag;
import org.skye.domain.InformationStoreDefinition;
import org.skye.domain.Project;

import java.util.*;

/**
 * The JPA Entity which will represent {@link org.skye.core.ObjectMetadata} for
 * persistence purposes.  It also contains methods which allow the Entity to
 * be copied from a non persisted {@link ObjectMetadata}, as well as a method
 * which copies this JPAObjectMetadata to an {@link ObjectMetadata}.
 */
@Entity
@Table(name = "OBJECT_METADATA")
@Data
public class JPAObjectMetadata
{
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;

    private String path;

    private String implementation;

    private String taskId;

    @OneToMany(cascade = CascadeType.REMOVE)
    private Set<Tag> tags;

    @OneToMany(cascade = CascadeType.REMOVE)
    private Map<String, String> metadata;

    private boolean container;

    private DateTime created;

    private DateTime ingested;

    private Project project;

    private InformationStoreDefinition informationStore;

    @OneToMany(cascade = CascadeType.REMOVE)
    private List<ArchiveContentBlock> archiveContentBlocks;

    /**
     * The default constructor for the JPAObjectMetadata class.
     */
    public JPAObjectMetadata()
    {
        this.path = "";
        this.implementation = "";
        this.taskId = "";
        this.tags = new HashSet<>();
        this.metadata = new HashMap<>();
        this.container = false;
        this.created = new DateTime();
        this.ingested = null;
        this.project = null;
        this.informationStore = null;
        this.archiveContentBlocks = new ArrayList<>();

        return;
    }

    /**
     * Creates the JPAObjectMetadata as a copy of an existing
     * {@link ObjectMetadata}.
     *
     * @param objectMetadata The {@link ObjectMetadata} to be copied.
     */
    public JPAObjectMetadata(ObjectMetadata objectMetadata)
    {
        this.id = objectMetadata.getId();
        this.implementation = objectMetadata.getImplementation();
        this.taskId = objectMetadata.getTaskId();
        this.tags = objectMetadata.getTags();
        this.metadata = objectMetadata.getMetadata();
        this.container = objectMetadata.isContainer();
        this.created = objectMetadata.getCreated();
        this.ingested = objectMetadata.getIngested();
        this.project = objectMetadata.getProject();
        this.informationStore = objectMetadata.getInformationStore();
        this.archiveContentBlocks = objectMetadata.getArchiveContentBlocks();
    }

    /**
     * Creates an {@link ObjectMetadata} copy of the JPA Entity version.
     *
     * @return An {@link ObjectMetadata} copy of this Entity.
     */
    public ObjectMetadata ToObjectMetadata()
    {
        ObjectMetadata objectMetadata = new ObjectMetadata();

        objectMetadata.setId(this.id);
        objectMetadata.setImplementation(this.implementation);
        objectMetadata.setTaskId(this.taskId);
        objectMetadata.setTags(this.tags);
        objectMetadata.setMetadata(this.metadata);
        objectMetadata.setContainer(this.container);
        objectMetadata.setCreated(this.created);
        objectMetadata.setIngested(this.ingested);
        objectMetadata.setProject(this.project);
        objectMetadata.setInformationStore(this.informationStore);
        objectMetadata.setArchiveContentBlocks(this.archiveContentBlocks);

        return objectMetadata;
    }
}
