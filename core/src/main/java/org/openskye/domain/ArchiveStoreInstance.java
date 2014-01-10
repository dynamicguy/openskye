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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An instance of an {@link org.openskye.core.ArchiveStore}.
 */
@Entity
@Table(name = "ARCHIVE_STORE")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = "id")
@ToString(exclude = "properties")
public class ArchiveStoreInstance implements Identifiable {
    /**
     * The {@link org.openskye.core.ArchiveStore}
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    /**
     * The {@link org.openskye.core.ArchiveStore} name
     */
    @NotNull
    @NotBlank
    @Column(unique=true)
    private String name;
    /**
     * The {@link org.openskye.core.ArchiveStore} instance implementation (for example: LocalFileSystem, JDBC, CIFS).
     */
    @NotNull
    @NotBlank
    private String implementation;
    /**
     * The {@link org.openskye.core.ArchiveStore} configuration properties. These properties will vary depending on the
     * type of {@link org.openskye.core.ArchiveStore} this is. For example, an {@link org.openskye.core.ArchiveStore} on
     * a local filesystem would have filesystem based properties, such as file paths, while one on a JDBC database would
     * have configuration properties, such as database name and address.
     */
    @ElementCollection
    @MapKeyColumn(name = "NAME")
    @Column(name = "VALUE")
    @CollectionTable(name = "ARCHIVE_STORE_PROPERTIES", joinColumns = @JoinColumn(name = "ARCHIVE_STORE_ID"))
    private Map<String, String> properties = new HashMap<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "archiveStoreInstance")
    @JsonIgnore
    private List<NodeArchiveStoreInstance> nodes = new ArrayList<>();

}
