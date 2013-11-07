package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Definition of an attribute for a metadata template
 */
@Entity
@Table(name = "ATTRIBUTE_DEFINITION")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(of = "id")
public class AttributeDefinition implements Identifiable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @ManyToOne
    @JoinColumn(name = "METADATA_TEMPLATE_ID")
    private MetadataTemplate metadataTemplate;
    @NotNull
    private String name;
    @NotNull
    private String shortLabel;
    private String description;
    // A flag that determines is the metadata is embedded in the simple object
    private boolean embedInObject;

}
