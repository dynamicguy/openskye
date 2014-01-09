package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * A set of properties for an {@link AuditLog}
 */
@Entity
@Table(name = "AUDIT_LOG_PROPERTY")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = "id")
public class AuditLogProperty implements Identifiable {
    /**
     * The audit log property id
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    /**
     * The {@link AuditLog} entry this property is attached to.
     */
    @ManyToOne
    private AuditLog auditLog;
    /**
     * The property name.
     */
    private String propertyName;
    /**
     * The property value.
     */
    private String propertyValue;

}
