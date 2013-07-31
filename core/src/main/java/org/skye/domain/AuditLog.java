package org.skye.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

/**
 * An entry in the Audit Log to track all changes on the platform
 */
@Entity
@Table(name = "AUDIT_LOG")
public class AuditLog extends AbstractDomainObject {

    private User user;
    private String auditEntity;
    private AuditEvent auditEvent;
    private List<AuditLogProperty> auditLogProperties;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAuditEntity() {
        return auditEntity;
    }

    public void setAuditEntity(String auditEntity) {
        this.auditEntity = auditEntity;
    }

    public AuditEvent getAuditEvent() {
        return auditEvent;
    }

    public void setAuditEvent(AuditEvent auditEvent) {
        this.auditEvent = auditEvent;
    }

    public List<AuditLogProperty> getAuditLogProperties() {
        return auditLogProperties;
    }

    public void setAuditLogProperties(List<AuditLogProperty> auditLogProperties) {
        this.auditLogProperties = auditLogProperties;
    }
}
