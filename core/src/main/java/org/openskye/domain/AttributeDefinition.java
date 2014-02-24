package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Definition of an attribute for a metadata template.
 */
@Entity
@Table(name = "ATTRIBUTE_DEFINITION")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = "id")
public class AttributeDefinition implements Identifiable {

    /**
     * <code>AttributeDefintion</code> id
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    /**
     * The {@link MetadataTemplate} this definition is applied to.
     */
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "METADATA_TEMPLATE_ID")
    private MetadataTemplate metadataTemplate;
    /**
     * The definition name
     */
    @NotNull
    @NotBlank
    @Column(unique=true)
    private String name;
    /**
     * A shortened version of the definition name
     */
    @NotNull
    @NotBlank
    private String shortLabel;
    /**
     * Description of this attribute definition
     */
    private String description;
    /**
     * A flag that determines if the metadata is embedded in the simple object.
     */
    private boolean embedInObject;
    /**
     * A flag that signifies if this attribute is optional
     */
    private boolean optional;
    /**
     * The type of attribute this definition applies to. For example text, numeric, date (see {@link AttributeType}).
     */
    @NotNull
    private AttributeType type;
    /**
     * A list of possible values that can be assigned to this attribute.
     */
    @ElementCollection
    @CollectionTable(
            name="ATTRIBUTE_DEFINITION_VALUES",
            joinColumns=@JoinColumn(name="ATTRIBUTE_DEFINITION_ID")
    )
    @Column(name="possibleValue")
    private List<String> possibleValues;

}
