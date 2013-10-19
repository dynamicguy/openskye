package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The representation of an information store owned by a {@link Domain}
 */
@Entity
@Table(name = "DOMAIN_INFORMATION_STORE")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@UuidGenerator(name = "InformationStoreDefinitionGenerator")
public class InformationStoreDefinition implements Identifiable {
    @Id
    @GeneratedValue(generator = "InformationStoreDefinitionGenerator")
    @Column(unique = true)
    private String id;
    private String name;
    @ManyToOne
    private Project project;
    // The name of the {@link InformationStore} implementation
    private String implementation;
    @Transient
    private Map<String, String> properties = new HashMap<>();

}
