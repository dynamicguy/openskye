package org.skye.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A role
 */
@Entity
@Table(name = "ROLE")
@Data
public class Role {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Permission> permissions = new ArrayList<>();

}
