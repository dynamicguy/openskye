package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

/**
 * A permission
 */
@Entity
@Table(name = "PERMISSION")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Permission {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;

    private String permission;

    @OneToMany
    private List<RolePermission> rolePermissions;


}
