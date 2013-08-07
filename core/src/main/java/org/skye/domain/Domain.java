package org.skye.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A domain represents a top level entity within the organization
 */
@Entity
@Table(name = "DOMAIN")
@Data
public class Domain {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    private String name;
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Project> projects = new ArrayList<>();

}
