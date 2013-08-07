package org.skye.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Relationship between a {@link Role} and a {@link Permission}
 */
@Entity
@Table(name = "ROLE_PERMISSION")
@Data
public class RolePermission {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    @ManyToOne
    private Role role;
    @ManyToOne
    private Permission permission;

}
