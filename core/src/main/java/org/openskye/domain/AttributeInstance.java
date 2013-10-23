package org.openskye.domain;

import lombok.Data;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;

/**
 * An instance of an attribute defined by an {@link AttributeDefinition}
 */
@Entity
@Table(name = "ATTRIBUTE_INSTANCE")
@Data
@UuidGenerator(name = "AttributeInstanceGenerator")
public class AttributeInstance implements Identifiable {

    @Id
    @GeneratedValue(generator = "AttributeInstanceGenerator")
    @Column(unique = true, length = 36)
    private String id;
    private MetadataOwnerType metadataOwnerType;
    private String ownerId;
    @ManyToOne
    @JoinColumn(name = "CHANNEL_ID")
    private Channel channel;
    @ManyToOne
    @JoinColumn(name = "ATTRIBUTE_DEFINITION_ID")
    private AttributeDefinition attributeDefinition;
    private String attributeValue;
}
