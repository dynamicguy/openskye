package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
 * The representation of an information store associated with a {@link Project}
 */
@Entity
@Table(name = "INFORMATION_STORE_DEFINITION")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = "id")
@ToString(exclude = "properties")
public class InformationStoreDefinition implements Identifiable {
    /**
     * The <code>InformationStore</code>'s unique id.
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    /**
     * The name of this <code>InformationStore</code>
     */
    @NotNull
    @NotBlank
    @Column(unique=true)
    private String name;
    /**
     * The <code>Project</code> that owns this {@link org.openskye.core.InformationStore}
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PROJECT_ID")
    private Project project;
    /**
     * The type of {@link org.openskye.core.InformationStore}. For example a JDBC database,or  a local file system.
     */
    private String implementation;
    @ElementCollection
    @MapKeyColumn(name = "NAME")
    @Column(name = "VALUE")
    @CollectionTable(name = "INFORMATION_STORE_DEFINITION_PROPERTIES", joinColumns = @JoinColumn(name = "INFORMATION_STORE_ID"))
    private Map<String, String> properties = new HashMap<>();

}
