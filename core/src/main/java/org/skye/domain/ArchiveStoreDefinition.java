package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

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
public class ArchiveStoreDefinition implements Identifiable {

    @Id
    @GeneratedValue(generator = "uuid")
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
