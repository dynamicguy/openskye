package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

/**
 * Relationship between a {@link Role} and a {@link Permission}
 */
@Entity
@Table(name = "ROLE_PERMISSION")
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RolePermission implements Identifiable {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(unique = true)
    private String id;
    @ManyToOne
    private Role role;
    @ManyToOne
    private Permission permission;

}
