package org.openskye.metadata;

import org.openskye.core.ObjectMetadata;
import org.openskye.core.SearchPage;
import org.openskye.domain.Project;

/**
 * This is the standard interface to allow the searching of {@link org.openskye.core.SimpleObject} instances
 * that have been discovered
 */
public interface ObjectMetadataSearch {

    /**
     * Perform a search over repository using a query.
     *
     * @param query The query.
     * @param searchPage Object describing the pagination for the returned result.
     *
     * @return The resulting {@link ObjectMetadata} instances.
     */
    Iterable<ObjectMetadata> search(String query, SearchPage searchPage);

    /**
     * Perform a search over repository for a single project using a query.
     *
     * @param project The project in which to search.
     * @param query   The query.
     * @param searchPage Object describing the pagination for the returned result.
     *
     * @return The resulting {@link ObjectMetadata} instances.
     */
    Iterable<ObjectMetadata> search(Project project, String query, SearchPage searchPage);

    /**
     * Add {@link ObjectMetadata} to the search
     *
     * @param objectMetadata the object metadata to index
     */
    void index(ObjectMetadata objectMetadata);

    /**
     * Clears the indexed items for the OMS.
     * <p/>
     * This is intended for testing and demo purposes.
     */
    void clear();

    /**
     * Ensures that all indexed entries are added to internal storage.
     * <p/>
     * This is intended for testing and demo purposes.
     */
    void flush();

}
