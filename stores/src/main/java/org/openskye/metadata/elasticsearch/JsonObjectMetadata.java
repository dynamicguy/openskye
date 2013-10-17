package org.openskye.metadata.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.joda.time.DateTime;
import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.Tag;
import org.openskye.domain.Project;

import java.util.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonObjectMetadata
{

    private String id;
    private String path;
    // Identify the implementation of the simple object
    private String implementation;
    // The taskId for the task that found/ingested the simple object
    private String taskId;
    @JsonIgnore
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
    @JsonIgnore
    private List<ArchiveContentBlock> archiveContentBlocks = new ArrayList<>();

    public JsonObjectMetadata()
    {

    }

    public JsonObjectMetadata(ObjectMetadata objectMetadata)
    {
        this.id = objectMetadata.getId();
        this.path = objectMetadata.getPath();
        this.implementation = objectMetadata.getImplementation();
        this.taskId = objectMetadata.getTaskId();
        this.tags = objectMetadata.getTags();
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
        this.archiveContentBlocks = objectMetadata.getArchiveContentBlocks();
    }

    public ObjectMetadata toObjectMetadata()
    {
        ObjectMetadata objectMetadata = new ObjectMetadata();

        objectMetadata.setId(this.id);
        objectMetadata.setPath(this.path);
        objectMetadata.setImplementation(this.implementation);
        objectMetadata.setTaskId(this.taskId);
        objectMetadata.setTags(this.tags);
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
        objectMetadata.setArchiveContentBlocks(this.archiveContentBlocks);

        return objectMetadata;
    }
}