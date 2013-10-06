package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * An instance of an {@link org.skye.core.ArchiveStore} definition
 */
@Entity
@Table(name = "ARCHIVE_STORE_INSTANCE")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@UuidGenerator(name = "ArchiveStoreInstanceGenerator")
public class ArchiveStoreInstance implements Identifiable {

    @Id
    @GeneratedValue(generator = "ArchiveStoreInstanceGenerator")
    @Column(unique = true)
    private String id;
    // The name of the {@link ArchiveStore} implementation
    private String implementation;
    @Transient
    private Map<String, String> properties = new HashMap<>();

}
