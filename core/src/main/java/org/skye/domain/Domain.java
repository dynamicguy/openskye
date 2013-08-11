package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Domain {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    private String name;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Project> projects = new ArrayList<>();
    @OneToMany(cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<DomainArchiveStore> archiveStores = new ArrayList<>();
    @OneToMany(cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<DomainInformationStore> informationStores = new ArrayList<>();

}
