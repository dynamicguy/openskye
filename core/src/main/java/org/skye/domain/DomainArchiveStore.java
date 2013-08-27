package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

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
public class DomainArchiveStore {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    private String name;
    private String description;
    @ManyToOne
    private Domain domain;
    @ManyToOne
    private ArchiveStoreInstance archiveStoreInstance;
    @Transient
    private Map<String, String> properties = new HashMap<>();
}
