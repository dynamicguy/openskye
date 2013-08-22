package org.skye.metadata;

import org.skye.core.SimpleObject;
import org.skye.domain.Domain;
import org.skye.domain.Project;
import org.skye.util.Page;

/**
 * This is the standard interface to allow the searching of {@link org.skye.core.SimpleObject} instances
 * that have been discovered
 */
public interface ObjectMetadataSearch {

    /**
     * Perform a search over repository using a query
     *
     * @param domain The domain in which to search
     * @param query  The query
     * @param page   The paging information
     * @return The resulting {@link org.skye.core.SimpleObject}s
     */
    Iterable<SimpleObject> search(Domain domain, String query, Page page);

    /**
     * Perform a search over repository for a single project using a query
     *
     * @param domain  The domain in which to search
     * @param project The project
     * @param query   The query
     * @param page    The paging information
     * @return The resulting {@link org.skye.core.SimpleObject}s
     */
    Iterable<SimpleObject> search(Domain domain, Project project, String query, Page page);

    /**
     * Add {@link SimpleObject} to the search
     *
     * @param simpleObject the simple object to index
     */
    void index(SimpleObject simpleObject);

}
