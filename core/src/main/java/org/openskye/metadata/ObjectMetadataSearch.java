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
     * Counts the results that will be produced by a query.
     * Many systems require pagination information like the number of
     * expected results, and the count operation can provide that.
     *
     *
     * @param query The query for which results are counted.
     *
     * @return The number of results a query will produce.
     */
    long count(String query);

    /**
     * Counts the results that will be produced by a query on a project.
     * Many systems require pagination information like the number of
     * expected results, and the count operation can provide that.
     *
     *
     * @param project The Project to be searched.
     *
     * @param query The query for which to perform the count.
     *
     * @return The number of results the query will produce.
     */
    long count(Project project, String query);

    /**
     * Attempts to count the results that the query.
     * {@link SearchPage} information is built based on the count,
     * and this information is used to query all results.
     *
     * @param query The query to be performed.
     *
     * @return All search results for the query.
     */
    Iterable<ObjectMetadata> search(String query);

    /**
     * Searches based on a given Project.
     *
     * Attempts to count the results that the query.
     * {@link SearchPage} information is built based on the count,
     * and this information is used to query all results.
     *
     * @param project The project to be searched.
     *
     * @param query The query to be performed.
     *
     * @return All search results for the query.
     */
    Iterable<ObjectMetadata> search(Project project, String query);

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
