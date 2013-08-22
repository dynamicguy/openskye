package org.skye.metadata;

import com.google.common.base.Optional;
import org.skye.core.ArchiveContentBlock;
import org.skye.core.SimpleObject;
import org.skye.domain.DomainInformationStore;

/**
 * This is the standard interface to allow the storage of {@link SimpleObject} instances
 * that have been discovered
 */
public interface ObjectMetadataRepository {

    /**
     * Gets the details of a single simple object
     *
     * @param id The identifier of the simple object
     * @return optionally the simple object
     */
    Optional<SimpleObject> get(String id);

    /**
     * Puts a simple object
     *
     * @param simpleObject
     */
    void put(SimpleObject simpleObject);

    /**
     * Returns the metadata for the archive content blocks for the simple object
     *
     * @param simpleObject
     * @return An iterable for the archive content blocks
     */
    Iterable<ArchiveContentBlock> getArchiveContentBlocks(SimpleObject simpleObject);

    /**
     * Returns an iterator over all the {@link SimpleObject} for the given {@link DomainInformationStore}
     *
     * @param domainInformationStore
     */
    Iterable<SimpleObject> getSimpleObjects(DomainInformationStore domainInformationStore);
}
