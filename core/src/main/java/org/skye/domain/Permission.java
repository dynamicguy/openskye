package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * A permission
 */
@Entity
@Table(name = "PERMISSION")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Permission implements Identifiable {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(unique = true)
    private String id;
    private String permission;
    @OneToMany
    private List<RolePermission> rolePermissions;


}
