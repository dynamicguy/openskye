package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * An instance of an attribute defined by an {@link AttributeDefinition}
 */
@Entity
@Table(name = "ATTRIBUTE_DEFINITION")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeInstance {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    private MetadataOwnerType metadataOwnerType;
    private String ownerId;
    @ManyToOne
    private AttributeDefinition attributeDefinition;
    private String attributeValue;
}
