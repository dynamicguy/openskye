package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

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
public class Role implements Identifiable {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(unique = true)
    private String id;
    @OneToMany(cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<RolePermission> rolePermissions = new ArrayList<>();
    private String name;

}
