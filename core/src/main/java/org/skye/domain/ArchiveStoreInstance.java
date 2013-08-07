package org.skye.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * An instance of an {@link org.skye.core.ArchiveStore} definition
 */
@Entity
@Table(name = "ARCHIVE_STORE_INSTANCE")
@Data
public class ArchiveStoreInstance {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    private String id;

}
