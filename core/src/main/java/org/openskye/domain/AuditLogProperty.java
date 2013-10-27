package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;

/**
 * A set of properties for an {@link AuditLog}
 */
@Entity
@Table(name = "AUDIT_LOG_PROPERTY")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@UuidGenerator(name = "AuditLogPropertyGenerator")
@EqualsAndHashCode(of = "id")
public class AuditLogProperty implements Identifiable {

    @Id
    @GeneratedValue(generator = "AuditLogPropertyGenerator")
    @Column(unique = true, length = 36)
    private String id;
    @ManyToOne
    private AuditLog auditLog;
    private String propertyName;
    private String propertyValue;

}
