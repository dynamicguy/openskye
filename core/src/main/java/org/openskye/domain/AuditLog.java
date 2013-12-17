package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;
import org.openskye.core.ObjectMetadata;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * An entry in the Audit Log to track all changes on the platform
 */
@Entity
@Table(name = "AUDIT_LOG")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(of = "id")
public class AuditLog implements Identifiable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @ManyToOne
    private User user;
    private String auditEntity;
    private AuditEvent auditEvent;
    private ObjectEvent objectEvent;
    @ManyToOne
    private ObjectMetadata objectAffected;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<AuditLogProperty> auditLogProperties = new ArrayList<>();

}
