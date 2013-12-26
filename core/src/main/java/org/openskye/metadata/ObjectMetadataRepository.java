package org.openskye.metadata;

import com.google.common.base.Optional;
import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.ObjectSet;
import org.openskye.core.SimpleObject;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.Project;
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
     * Based on the checksum and original size we look to see if we have already got a
     * copy of that content in the archive
     *
     * @param checksum
     * @param originalSize
     * @return Optionally return the existing ACB
     */
    Optional<ArchiveContentBlock> getArchiveContentBlock(String checksum, long originalSize);

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
    void removeObjectFromSet(ObjectSet objectSet, ObjectMetadata objectMetadata);

    /**
     * Determines if the {@link ObjectMetadata} is already included in the
     * {@link ObjectSet}.
     *
     * @param objectSet      The {@link ObjectSet} against which the query will run.
     * @param objectMetadata The {@link ObjectMetadata} for which the query is
     *                       run.
     * @return True if the {@link ObjectMetadata} is found in the
     * {@link ObjectSet}, or false if it is not.
     */
    boolean isObjectInSet(ObjectSet objectSet, ObjectMetadata objectMetadata);

    /**
     * Determines if the {@link ObjectMetadata} is already included in the
     * {@link ObjectMetadataRepository}.
     *
     * @param objectMetadata The {@link ObjectMetadata} for which the query is
     *                       run.
     * @return True if the {@link ObjectMetadata} is found in the
     * {@link ObjectMeta}, or false if it is not.
     */
    boolean isObjectInOMR(ObjectMetadata objectMetadata);

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
     * @param objectMetadata The new instance to be created.
     * @return The newly created instance with its id set.
     */
    ObjectMetadata put(ObjectMetadata objectMetadata);

    /**
     * Returns an iterator over all the {@link ObjectMetadata} for the given {@link org.openskye.domain.Project}
     *
     * @param project
     */
    Iterable<ObjectMetadata> getObjects(Project project);

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
     * Gets a list of all {@link ObjectSet} instances.
     *
     * @return An {@link Iterable} collection of {@link ObjectSet} instances.
     */
    Iterable<ObjectSet> getAllObjectSets();

    /**
     * Gets a list of all {@link ObjectMetadata} instances.
     *
     * @return An {@link Iterable} collection of {@link ObjectMetadata}
     * instances.
     */
    Iterable<ObjectMetadata> getAllObjects();

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

    /**
     * Updates the given {@link org.openskye.core.ObjectSet}
     *
     * @param objectSet the object set to update
     */
    void updateObjectSet(Optional<ObjectSet> objectSet);
}
