package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
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
public class ArchiveStoreDefinition implements Identifiable {

    @Id
    @GeneratedValue(generator = "ArchiveStoreDefinitionGenerator")
    @Column(unique = true,length = 36)
    private String id;
    private String name;
    private String description;
    @ManyToOne
    private Project project;
    @ManyToOne
    private ArchiveStoreInstance archiveStoreInstance;
    @Transient
    private Map<String, String> properties = new HashMap<>();
}
