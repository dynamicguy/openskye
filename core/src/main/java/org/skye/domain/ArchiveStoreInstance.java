package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

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
public class ArchiveStoreInstance {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    private String id;
    // The name of the {@link ArchiveStore} implementation
    private String implementation;
    @MapKey(name = "name")
    private Map<String, String> properties = new HashMap<>();

}
