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
@Table(name = "DOMAIN_ARCHIVE_STORE")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@UuidGenerator(name = "ArchiveStoreDefinitionGenerator")
public class ArchiveStoreDefinition implements Identifiable {

    @Id
    @GeneratedValue(generator = "ArchiveStoreDefinitionGenerator")
    @Column(unique = true)
    private String id;
    private String name;
    private String description;
    @ManyToOne
    private Domain domain;
    @ManyToOne
    private ArchiveStoreInstance archiveStoreInstance;
    @Transient
    private Map<String, String> properties = new HashMap<>();
}
