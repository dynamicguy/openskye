package org.openskye.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;
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
public class ArchiveContentBlock {

    // This is the unique id across all nodes
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @NotBlank
    @NotNull
    private String archiveStoreInstanceId;
    @NotBlank
    @NotNull
    private String checksum;
    @NotNull
    private long originalSize;
    @NotNull
    private long compressedSize;
    @ManyToMany(mappedBy="archiveContentBlocks")
    @JsonIgnore
    private List<ObjectMetadata> objectMetadataReferences = new ArrayList<>();
    @ManyToMany(mappedBy="archiveContentBlocks")
    @JsonIgnore
    private List<Node> nodes = new ArrayList<>();

}
