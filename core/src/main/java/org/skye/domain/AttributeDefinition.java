package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;

/**
 * Definition of an attribute for a metadata template
 */
@Entity
@Table(name = "ATTRIBUTE_DEFINITION")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@UuidGenerator(name = "AttributeDefinitionGenerator")
public class AttributeDefinition implements Identifiable {

    @Id
    @GeneratedValue(generator = "AttributeDefinitionGenerator")
    @Column(unique = true)
    private String id;
    private String name;
    private String shortLabel;
    private String description;
    // A flag that determines is the metadata is embedded in the archive
    private boolean embedInArchive;

}
