package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

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
@EqualsAndHashCode(of = "id")
@ToString(exclude = "properties")
public class InformationStoreDefinition implements Identifiable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @NotNull
    @NotBlank
    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PROJECT_ID")
    private Project project;
    // The name of the {@link InformationStore} implementation
    private String implementation;
    @ElementCollection
    @MapKeyColumn(name = "NAME")
    @Column(name = "VALUE")
    @CollectionTable(name = "INFORMATION_STORE_DEFINITION_PROPERTIES", joinColumns = @JoinColumn(name = "INFORMATION_STORE_ID"))
    private Map<String, String> properties = new HashMap<>();

}
