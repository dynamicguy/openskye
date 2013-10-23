package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.eclipse.persistence.annotations.UuidGenerator;

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
@UuidGenerator(name = "DomainGenerator")
public class Domain implements Identifiable {
    @Id
    @GeneratedValue(generator = "DomainGenerator")
    @Column(unique = true, length = 36)
    private String id;
    @NotNull
    private String name;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Project> projects = new ArrayList<>();
    @OneToMany(cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<MetadataTemplate> metadataTemplates = new ArrayList<>();

}
