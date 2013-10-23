package org.openskye.domain;

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
    @Column(unique = true, length = 36)
    private String id;
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "DOMAIN_ID")
    private Domain domain;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "metadataTemplate")
    @JsonIgnore
    private List<AttributeDefinition> attributeDefinitions = new ArrayList<>();

}
