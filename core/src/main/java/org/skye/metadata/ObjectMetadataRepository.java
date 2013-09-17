package org.skye.metadata;

import com.google.common.base.Optional;
import org.skye.core.ObjectMetadata;
import org.skye.core.ObjectSet;
import org.skye.core.SimpleObject;
import org.skye.domain.InformationStoreDefinition;
import org.skye.domain.Task;

/**
 * This is the standard interface to allow the storage of {@link SimpleObject} instances
 * that have been discovered
 */
public interface ObjectMetadataRepository {

    /**
     * Creates a new {@link ObjectSet}
     *
     * @return a new object set
     */
    ObjectSet createObjectSet();

    /**
     * Removes the {@link ObjectSet} from the OMR
     *
     * @param objectSet the object set to remove
     */
    void deleteObjectSet(ObjectSet objectSet);

    /**
     * Adds an {@link ObjectMetadata} to a given {@link ObjectSet}
     *
     * @param objectSet      the object set
     * @param objectMetadata the object metadata to add
     */
    void addObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata);

    /**
     * Removes an {@link ObjectMetadata} to a given {@link ObjectSet}
     *
     * @param objectSet      the object set
     * @param objectMetadata the object metadata to remove
     */
    void removeObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata);

    /**
     * Gets the details of a single object's metadata
     *
     * @param id The identifier of the object
     * @return optionally the object
     */
    Optional<ObjectMetadata> get(String id);

    /**
     * Puts a object metadata
     *
     * @param objectMetadata
     */
    void put(ObjectMetadata objectMetadata);

    /**
     * Returns an iterator over all the {@link ObjectMetadata} for the given {@link org.skye.domain.InformationStoreDefinition}
     *
     * @param informationStoreDefinition
     */
    Iterable<ObjectMetadata> getObjects(InformationStoreDefinition informationStoreDefinition);

    /**
     * Returns an iterator over all the {@link ObjectMetadata} for the given {@link Task}
     *
     * @param task
     */
    Iterable<ObjectMetadata> getObjects(Task task);

    /**
     * Returns an iterator over all the {@link ObjectMetadata} for the given {@link ObjectSet}
     *
     * @param objectSet
     */
    Iterable<ObjectMetadata> getObjects(ObjectSet objectSet);


}
