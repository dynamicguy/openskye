package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * A domain represents top level resources within the organization.
 */
@Entity
@Table(name = "DOMAIN")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"projects", "metadataTemplates"})
public class Domain implements Identifiable {
    /**
     * This <code>Domain</code>'s unique id
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    /**
     * The name of this <code>Domain</code>
     */
    @NotNull
    @NotBlank
    @Column(unique=true)
    private String name;
    /**
     * The {@link Project}s in this domain.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "domain")
    @JsonIgnore
    private List<Project> projects = new ArrayList<>();
    /**
     * The {@link MetadataTemplate}s used by objects managed by projects in this domain.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "domain")
    @JsonIgnore
    private List<MetadataTemplate> metadataTemplates = new ArrayList<>();

}
