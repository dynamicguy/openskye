package org.skye.metadata.impl.jpa;

import org.joda.time.DateTime;
import org.skye.domain.Project;

import javax.persistence.metamodel.*;

@StaticMetamodel(JPAObjectMetadata.class)
public class JPAObjectMetadata_ {
    public static volatile SingularAttribute<JPAObjectMetadata, String> id;
    public static volatile SingularAttribute<JPAObjectMetadata, String> path;
    public static volatile SingularAttribute<JPAObjectMetadata, String> implementation;
    public static volatile SingularAttribute<JPAObjectMetadata, String> taskId;
    public static volatile SetAttribute<JPAObjectMetadata, JPATag> tags;
    public static volatile MapAttribute<JPAObjectMetadata, String, String> metadata;
    public static volatile SingularAttribute<JPAObjectMetadata, Boolean> container;
    public static volatile SingularAttribute<JPAObjectMetadata, DateTime> created;
    public static volatile SingularAttribute<JPAObjectMetadata, DateTime> ingested;
    public static volatile SingularAttribute<JPAObjectMetadata, DateTime> lastModified;
    public static volatile SingularAttribute<JPAObjectMetadata, Project> project;
    public static volatile SingularAttribute<JPAObjectMetadata, Long> originalSize;
    public static volatile SingularAttribute<JPAObjectMetadata, Long> archiveSize;
    public static volatile SingularAttribute<JPAObjectMetadata, String> mimeType;
    public static volatile SingularAttribute<JPAObjectMetadata, String> checksum;
    public static volatile SingularAttribute<JPAObjectMetadata, String> informationStoreId;
    public static volatile ListAttribute<JPAObjectMetadata, JPAArchiveContentBlock> archiveContentBlocks;
}
