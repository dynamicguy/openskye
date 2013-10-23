package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import java.util.List;

/**
 * A permission
 */
@Entity
@Table(name = "PERMISSION")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@UuidGenerator(name = "PermissionGenerator")
public class Permission implements Identifiable {
    @Id
    @GeneratedValue(generator = "PermissionGenerator")
    @Column(unique = true, length = 36)
    private String id;
    private String permission;
    @OneToMany
    private List<RolePermission> rolePermissions;


}
