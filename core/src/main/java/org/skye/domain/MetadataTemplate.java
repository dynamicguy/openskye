package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "METADATA_TEMPLATE")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@UuidGenerator(name = "MetadataTemplateGenerator")
public class MetadataTemplate implements Identifiable {
    @Id
    @GeneratedValue(generator = "MetadataTemplateGenerator")
    @Column(unique = true)
    private String id;
    private String name;
    private String description;
    @ManyToOne
    private Domain domain;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Permission> fieldDefinitions = new ArrayList<>();

}
