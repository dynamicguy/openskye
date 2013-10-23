package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eclipse.persistence.annotations.UuidGenerator;

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
@UuidGenerator(name = "RolePermissionGenerator")
public class RolePermission implements Identifiable {
    @Id
    @GeneratedValue(generator = "RolePermissionGenerator")
    @Column(unique = true, length = 36)
    private String id;
    @ManyToOne
    @JoinColumn(name = "ROLE_ID")
    private Role role;
    @ManyToOne
    @JoinColumn(name = "PERMISSION_ID")
    private Permission permission;

}
