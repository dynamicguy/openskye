package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eclipse.persistence.annotations.UuidGenerator;

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
@UuidGenerator(name = "AuditLogGenerator")
@EqualsAndHashCode(of = "id")
public class AuditLog implements Identifiable {

    @Id
    @GeneratedValue(generator = "AuditLogGenerator")
    @Column(unique = true, length = 36)
    private String id;
    @ManyToOne
    private User user;
    private String auditEntity;
    private AuditEvent auditEvent;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<AuditLogProperty> auditLogProperties = new ArrayList<>();

}
