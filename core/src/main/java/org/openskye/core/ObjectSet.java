package org.openskye.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;
import org.openskye.domain.Identifiable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
public class ObjectSet implements Identifiable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    private String id;
    @NotBlank
    @NotNull
    @Column(unique=true)
    private String name;
    private ObjectSetType type;
    private String query;
    private boolean onHold = false;
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
