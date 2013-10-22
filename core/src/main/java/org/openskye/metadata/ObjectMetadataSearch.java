package org.openskye.metadata;

import org.openskye.core.ObjectMetadata;
import org.openskye.domain.Domain;
import org.openskye.domain.Project;
import org.openskye.util.Page;

/**
 * This is the standard interface to allow the searching of {@link org.openskye.core.SimpleObject} instances
 * that have been discovered
 */
public interface ObjectMetadataSearch {

    /**
     * Perform a search over repository using a query
     *
     * @param domain The domain in which to search
     * @param query  The query
     * @param page   The paging information
     * @return The resulting {@link org.openskye.core.ObjectMetadata}s
     */
    Iterable<ObjectMetadata> search(Domain domain, String query, Page page);

    /**
     * Perform a search over repository for a single project using a query
     *
     * @param domain  The domain in which to search
     * @param project The project
     * @param query   The query
     * @param page    The paging information
     * @return The resulting {@link org.openskye.core.ObjectMetadata}s
     */
    Iterable<ObjectMetadata> search(Domain domain, Project project, String query, Page page);

    /**
     * Add {@link ObjectMetadata} to the search
     *
     * @param objectMetadata the object metadata to index
     */
    void index(ObjectMetadata objectMetadata);

    /**
     * Clears the indexed items for the OMS.
     *
     * This is intended for testing and demo purposes.
     */
    void clear();

    /**
     * Ensures that all indexed entries are added to internal storage.
     *
     * This is intended for testing and demo purposes.
     */
    void flush();

}
