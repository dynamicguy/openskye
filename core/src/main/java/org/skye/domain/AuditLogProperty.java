package org.skye.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A set of properties for an {@link AuditLog}
 */
@Entity
@Table(name = "AUDIT_LOG_PROPERTY")
@Data
public class AuditLogProperty {

    private AuditLog auditLog;
    private String propertyName;
    private String propertyValue;

}
