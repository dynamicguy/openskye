package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * An entry in the Audit Log to track all changes on the platform.
 */
@Entity
@Table(name = "AUDIT_LOG")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = "id")
public class AuditLog implements Identifiable {
    /**
     * The id for this audit log entry
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    /**
     * The user who caused the audited event to occur.
     */
    @ManyToOne
    private User user;
    /**
     * The the class name of the Skye component operated on in this event.
     */
    private String auditEntity;
    /**
     * The type of event that occured. See {@link AuditEvent}
     */
    private AuditEvent auditEvent;
    /**
     * The type of object specific event that occured. This value is 0 if the audit event did not affect a
     * <code>SimpleObject</code>. of some kind. See {@link ObjectEvent}.
     */
    private ObjectEvent objectEvent;
    /**
     * The id of the SimpleObject affected by the event, if any. This value is null if the event did not affect any
     * <code>SimpleObject</code>s.
     */
    private String objectAffected;
    /**
     * The date this entry in the audit log was created.
     */
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();
    /**
     * Properties of this audit log entry.
     */
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<AuditLogProperty> auditLogProperties = new ArrayList<>();

}
