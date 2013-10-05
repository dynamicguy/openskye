package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

/**
 * An instance of an attribute defined by an {@link AttributeDefinition}
 */
@Entity
@Table(name = "ATTRIBUTE_DEFINITION")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeInstance implements Identifiable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(unique = true)
    private String id;
    private MetadataOwnerType metadataOwnerType;
    private String ownerId;
    @ManyToOne
    private Channel channel;
    @ManyToOne
    private AttributeDefinition attributeDefinition;
    private String attributeValue;
}
