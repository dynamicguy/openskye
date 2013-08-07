package org.skye.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * An entry in the Audit Log to track all changes on the platform
 */
@Entity
@Table(name = "AUDIT_LOG")
@Data
public class AuditLog {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    private User user;
    private String auditEntity;
    private AuditEvent auditEvent;
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<AuditLogProperty> auditLogProperties = new ArrayList<>();

}
