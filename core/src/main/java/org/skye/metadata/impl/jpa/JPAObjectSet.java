package org.skye.metadata.impl.jpa;

import lombok.Data;
import org.eclipse.persistence.annotations.UuidGenerator;
import org.skye.core.ObjectSet;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A JPA persistable entity which represents an {@link ObjectSet}, complete
 * with methods to translate between the two types.
 * <p/>
 * This object will tie an Id to a list of ids for
 * {@link org.skye.core.ObjectMetadata} instances, which will be stored
 * in the dataset.  The {@link JPAObjectMetadataRepository} will allow
 * the user to retrieve or act upon each object one at a time, so that
 * a very large dataset is not pulled into memory all at once.
 */
@Entity
@Table(name = "OBJECT_SET")
@Data
@UuidGenerator(name = "ObjectSetGenerator")
public class JPAObjectSet {
    @Id
    @GeneratedValue(generator = "ObjectSetGenerator")
    @Column(unique = true)
    private String id;
    private String name;
    @ManyToMany
    @JoinTable(
            name = "OBJECT_SET_TO_MEATDATA",
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
    private Set<JPAObjectMetadata> objectMetadataSet;

    /**
     * Default Constructor.
     */
    public JPAObjectSet() {
        this.name = "";
        this.objectMetadataSet = new HashSet<>();

        return;
    }

    /**
     * Creates a JPAObjectSet translation of the given {@link ObjectSet}, so
     * that the set can be persisted in a datastore.
     *
     * @param objectSet The {@link ObjectSet} to be persisted.
     */
    public JPAObjectSet(ObjectSet objectSet) {
        this.id = objectSet.getId();
        this.name = objectSet.getName();

        this.objectMetadataSet = new HashSet<>();

        return;
    }

    /**
     * Translates the persistable JPAObjectSet into a generalized
     * {@link ObjectSet} which can be utilized by the rest of the Skye API.
     *
     * @return An {@link ObjectSet} copy of the instance.
     */
    public ObjectSet toObjectSet() {
        ObjectSet objectSet = new ObjectSet();

        objectSet.setId(this.id);
        objectSet.setName(this.name);

        return objectSet;
    }
}
