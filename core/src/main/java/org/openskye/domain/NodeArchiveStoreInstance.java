package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * A node is the representation of a node is the storage deployment
 * <p/>
 * Nodes are linked to {@link org.openskye.domain.ArchiveStoreInstance} and provide the ability to
 * define a system on which a worker is running
 */
@Entity
@Table(name = "NODE_ARCHIVE_STORE_INSTANCE")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"archiveStoreInstance","node"})
public class NodeArchiveStoreInstance implements Identifiable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "NODE_ID")
    private Node node;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ARCHIVE_STORE_INSTANCE_ID")
    @JsonBackReference("nodes")
    private ArchiveStoreInstance archiveStoreInstance;
    @NotNull
    private NodeRole nodeRole = NodeRole.PRIMARY;
}
