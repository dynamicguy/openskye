package org.openskye.core;

import org.joda.time.DateTime;
import org.openskye.domain.Project;

import javax.persistence.metamodel.*;

@StaticMetamodel(ObjectMetadata.class)
public class ObjectMetadata_ {
    public static volatile SingularAttribute<ObjectMetadata, String> id;
    public static volatile SingularAttribute<ObjectMetadata, String> path;
    public static volatile SingularAttribute<ObjectMetadata, String> implementation;
    public static volatile SingularAttribute<ObjectMetadata, String> taskId;
    public static volatile SetAttribute<ObjectMetadata, Tag> tags;
    public static volatile MapAttribute<ObjectMetadata, String, String> metadata;
    public static volatile SingularAttribute<ObjectMetadata, Boolean> container;
    public static volatile SingularAttribute<ObjectMetadata, DateTime> created;
    public static volatile SingularAttribute<ObjectMetadata, DateTime> ingested;
    public static volatile SingularAttribute<ObjectMetadata, DateTime> lastModified;
    public static volatile SingularAttribute<ObjectMetadata, Project> project;
    public static volatile SingularAttribute<ObjectMetadata, Long> originalSize;
    public static volatile SingularAttribute<ObjectMetadata, Long> archiveSize;
    public static volatile SingularAttribute<ObjectMetadata, String> mimeType;
    public static volatile SingularAttribute<ObjectMetadata, String> checksum;
    public static volatile SingularAttribute<ObjectMetadata, String> informationStoreId;
    public static volatile ListAttribute<ObjectMetadata, ArchiveContentBlock> archiveContentBlocks;
}
