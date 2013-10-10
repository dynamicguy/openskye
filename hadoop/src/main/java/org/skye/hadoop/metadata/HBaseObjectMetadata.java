package org.skye.hadoop.metadata;

import lombok.Data;
import org.joda.time.DateTime;
import org.skye.domain.Project;
import org.skye.metadata.impl.jpa.JPAArchiveContentBlock;
import org.skye.metadata.impl.jpa.JPAObjectMetadata;
import org.skye.metadata.impl.jpa.JPATag;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "OBJECT_METADATA", schema = "skye@hbase")
@Data
public class HBaseObjectMetadata extends JPAObjectMetadata{
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
    private DateTime created = new DateTime();
    private DateTime ingested = new DateTime();
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
}
