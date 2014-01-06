package org.openskye.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * An instance of an attribute defined by an {@link AttributeDefinition}.
 */
@Entity
@Table(name = "ATTRIBUTE_INSTANCE")
@Data
@EqualsAndHashCode(of = "id")
public class AttributeInstance implements Identifiable {
    /**
     * The id for this attribute.
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
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
