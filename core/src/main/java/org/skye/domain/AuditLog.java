package org.skye.domain;

import com.google.common.base.Objects;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

/**
 * An entry in the Audit Log to track all changes on the platform
 */
@Entity
@Table(name = "AUDIT_LOG")
public class AuditLog {

    @Id
    @GeneratedValue(generator = "hibernate-uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    private User user;
    private String auditEntity;
    private AuditEvent auditEvent;
    private List<AuditLogProperty> auditLogProperties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof AuditLog) {
            final AuditLog other = (AuditLog) obj;
            return Objects.equal(id, other.id);
        } else {
            return false;
        }
    }

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
