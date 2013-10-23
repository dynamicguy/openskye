package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * The representation of an information store owned by a {@link Domain}
 */
@Entity
@Table(name = "INFORMATION_STORE_DEFINITION")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@UuidGenerator(name = "InformationStoreDefinitionGenerator")
public class InformationStoreDefinition implements Identifiable {
    @Id
    @GeneratedValue(generator = "InformationStoreDefinitionGenerator")
    @Column(unique = true, length = 36)
    private String id;
    @NotNull
    private String name;
    @ManyToOne
    private Project project;
    // The name of the {@link InformationStore} implementation
    private String implementation;
    @Transient
    private Map<String, String> properties = new HashMap<>();

}
