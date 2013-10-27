package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Domain} owned {@link ArchiveStoreInstance}
 */
@Entity
@Table(name = "ARCHIVE_STORE_DEFINITION")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@UuidGenerator(name = "ArchiveStoreDefinitionGenerator")
@EqualsAndHashCode(of = "id")
public class ArchiveStoreDefinition implements Identifiable {

    @Id
    @GeneratedValue(generator = "ArchiveStoreDefinitionGenerator")
    @Column(unique = true, length = 36)
    private String id;
    @NotNull
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "PROJECT_ID")
    private Project project;
    @ManyToOne
    @JoinColumn(name = "ARCHIVE_STORE_ID")
    private ArchiveStoreInstance archiveStoreInstance;
    @ElementCollection
    @MapKeyColumn(name = "NAME")
    @Column(name = "VALUE")
    @CollectionTable(name = "ARCHIVE_STORE_DEFINITION_PROPERTIES", joinColumns = @JoinColumn(name = "ARCHIVE_STORE_ID"))
    private Map<String, String> properties = new HashMap<>();
}
