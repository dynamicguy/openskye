package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A role
 */
@Entity
@Table(name = "ROLE")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@UuidGenerator(name = "RoleGenerator")
public class Role implements Identifiable {
    @Id
    @GeneratedValue(generator = "RoleGenerator")
    @Column(unique = true)
    private String id;
    @OneToMany(cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<RolePermission> rolePermissions = new ArrayList<>();
    private String name;

}
