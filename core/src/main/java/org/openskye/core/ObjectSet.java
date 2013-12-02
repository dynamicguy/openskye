package org.openskye.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * An object set is a collection of {@link SimpleObject} that are grouped together
 * for a purpose
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "OBJECT_SET")
@EqualsAndHashCode(of = "id")
public class ObjectSet {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    private String id;
    private String name;
    @ManyToMany
    @JoinTable(
            name = "OBJECT_SET_TO_METADATA",
            joinColumns = {
                    @JoinColumn(
                            name = "setId",
                            referencedColumnName = "id"
                    )
            },
            inverseJoinColumns = {
                    @JoinColumn(
                            name = "metadataId",
                            referencedColumnName = "id"
                    )
            }
    )
    private Set<ObjectMetadata> objectMetadataSet = new HashSet<>();
}
