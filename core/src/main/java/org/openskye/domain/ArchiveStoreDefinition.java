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
 * An {@link ArchiveStoreInstance} associated with a specific project.
 */
@Entity
@Table(name = "ARCHIVE_STORE_DEFINITION")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(of = "id")
@ToString(exclude="properties")
public class ArchiveStoreDefinition implements Identifiable {

    /**
     * The <code>ArchiveStoreDefinition</code> id
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    /**
     * <code>ArchiveStoreDefinition</code> name
     */
    @NotNull
    @NotBlank
    @Column(unique=true)
    private String name;
    /**
     * Description of the <code>ArchiveStoreDefinition</code>.
     */
    private String description;
    /**
     * The <code>Project</code> this <code>ArchiveStoreDefinition</code> is attached to
     */
    @ManyToOne
    @JoinColumn(name = "PROJECT_ID")
    private Project project;
    /**
     * The <code>ArchiveStoreInstance</code> attached to this definition
     */
    @ManyToOne
    @JoinColumn(name = "ARCHIVE_STORE_ID")
    private ArchiveStoreInstance archiveStoreInstance;
    /**
     * Various configuration properties
     */
    @ElementCollection
    @MapKeyColumn(name = "NAME")
    @Column(name = "VALUE")
    @CollectionTable(name = "ARCHIVE_STORE_DEFINITION_PROPERTIES", joinColumns = @JoinColumn(name = "ARCHIVE_STORE_ID"))
    private Map<String, String> properties = new HashMap<>();
}
