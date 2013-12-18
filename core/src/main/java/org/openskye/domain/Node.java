package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
    @ManyToOne
    @NotNull
    private ArchiveStoreInstance archiveStoreInstance;
    @NotNull
    private NodeRole nodeRole;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @Column(name = "LAST_CONNECTED")
    private LocalDateTime lastConnected;
    private String lastHostName;
    private String lastIpAddress;
}
