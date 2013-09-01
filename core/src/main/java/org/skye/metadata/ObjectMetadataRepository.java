package org.skye.metadata;

import com.google.common.base.Optional;
import org.skye.core.ObjectMetadata;
import org.skye.core.SimpleObject;
import org.skye.domain.DomainInformationStore;

/**
 * This is the standard interface to allow the storage of {@link SimpleObject} instances
 * that have been discovered
 */
public interface ObjectMetadataRepository {

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
     * Returns an iterator over all the {@link ObjectMetadata} for the given {@link DomainInformationStore}
     *
     * @param domainInformationStore
     */
    Iterable<ObjectMetadata> getObjects(DomainInformationStore domainInformationStore);
}
