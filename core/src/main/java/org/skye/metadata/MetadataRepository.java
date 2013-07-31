package org.skye.metadata;

import com.google.common.base.Optional;
import org.skye.core.SimpleObject;
import org.skye.domain.Domain;
import org.skye.domain.Project;
import org.skye.util.Page;

/**
 * This is the standard interface to allow the storage and searching of {@link SimpleObject} instances
 * that have been discovered
 */
public interface MetadataRepository {


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
     * Perform a search over repository using a query
     *
     * @param domain The domain in which to search
     * @param query  The query
     * @param page   The paging information
     * @return The resulting {@link SimpleObject}s
     */
    Iterable<SimpleObject> search(Domain domain, String query, Page page);

    /**
     * Perform a search over repository for a single project using a query
     *
     * @param domain  The domain in which to search
     * @param project The project
     * @param query   The query
     * @param page    The paging information
     * @return The resulting {@link SimpleObject}s
     */
    Iterable<SimpleObject> search(Domain domain, Project project, String query, Page page);


}
