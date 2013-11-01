package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * A domain represents a top level resources within the organization
 */
@Entity
@Table(name = "DOMAIN")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"projects", "metadataTemplates"})
public class Domain implements Identifiable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @NotNull
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "domain")
    @JsonIgnore
    private List<Project> projects = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "domain")
    @JsonIgnore
    private List<MetadataTemplate> metadataTemplates = new ArrayList<>();

}
