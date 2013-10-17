package org.openskye.metadata;

import com.google.common.base.Optional;
import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.ObjectSet;
import org.openskye.core.SimpleObject;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.Task;

/**
 * This is the standard interface to allow the storage of {@link SimpleObject} instances
 * that have been discovered
 */
public interface ObjectMetadataRepository {

    /**
     * Creates a new {@link ObjectSet}
     *
     * @param name The name to be used for the {@link ObjectSet}.
     * @return a new object set
     */
    ObjectSet createObjectSet(String name);

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
     * Determines if the {@link ObjectMetadata} is already included in the
     * {@link ObjectSet}.
     *
     * @param objectSet      The {@link ObjectSet} against which the query will run.
     * @param objectMetadata The {@link ObjectMetadata} for which the query is
     *                       run.
     * @return True if the {@link ObjectMetadata} is found in the
     *         {@link ObjectSet}, or false if it is not.
     */
    boolean isObjectInSet(ObjectSet objectSet, ObjectMetadata objectMetadata);

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
     * Returns an iterator over all the {@link ObjectMetadata} for the given {@link org.openskye.domain.InformationStoreDefinition}
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

    /**
     * Returns an instance of an {@link ObjectSet} from its id
     *
     * @param objectSetId the id of the object set fo lookup
     * @return the objectset if found
     */
    Optional<ObjectSet> getObjectSet(String objectSetId);

    /**
     * Returns an instance of the {@link InformationStoreDefinition} which represents the
     * source {@link InformationStoreDefinition} for this {@link ObjectMetadata}
     *
     * @param objectMetadata the object metadata to find the source for
     * @return The information store definition
     */
    InformationStoreDefinition getSourceInformationStoreDefinition(ObjectMetadata objectMetadata);

    /**
     * Returns the instance of the {@link ArchiveStoreDefinition} that is linked to this
     * {@link ArchiveContentBlock}
     *
     * @param archiveContentBlock The archive content block to find the archive store definition for
     * @return the archive store definition
     */
    ArchiveStoreDefinition getArchiveStoreDefinition(ArchiveContentBlock archiveContentBlock);
}
