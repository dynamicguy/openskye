package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A node is the representation of a node is the storage deployment
 * <p/>
 * Nodes are linked to {@link ArchiveStoreInstance} and provide the ability to
 * define a system on which a worker is running
 */
@Entity
@Table(name = "NODE")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(of = "id")
public class Node implements Identifiable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "node")
    @JsonIgnore
    private List<NodeArchiveStoreInstance> nodeArchiveStoreInstances = new ArrayList<>();
    @NaturalId
    private String hostname;
    private String serviceAccount;
}
