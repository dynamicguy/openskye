package org.openskye.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.openskye.domain.ArchiveStoreInstance;
import org.openskye.domain.Node;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * A block of content that relates to one or more {@link SimpleObject}
 * and has the internal identifier for the storage of that block of
 * content within the archive store
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Embeddable
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "ARCHIVE_CONTENT_BLOCK")
@ToString(exclude = {"nodes", "objectMetadataReferences"})
public class ArchiveContentBlock {

    // This is the unique id across all nodes
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @NotNull
    @ManyToOne
    private ArchiveStoreInstance archiveStoreInstance;
    // TODO we need to validate these
    private String checksum;
    private long originalSize;
    private long compressedSize;
    @ManyToMany(mappedBy = "archiveContentBlocks")
    @JsonIgnore
    private List<ObjectMetadata> objectMetadataReferences = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "ARCHIVE_CONTENT_BLOCK_NODE",
            joinColumns = {@JoinColumn(name = "ARCHIVE_CONTENT_BLOCK_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "NODE_ID", referencedColumnName = "ID")})
    @JsonIgnore
    private List<Node> nodes = new ArrayList<>();

}
