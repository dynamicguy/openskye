package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * A set of properties for an {@link AuditLog}
 */
@Entity
@Table(name = "AUDIT_LOG_PROPERTY")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditLogProperty implements Identifiable
{

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(unique = true)
    private String id;
    @ManyToOne
    private AuditLog auditLog;
    private String propertyName;
    private String propertyValue;

}
