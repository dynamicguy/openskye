package org.openskye.core;

/**
 * Representation of a store, either an {@link InformationStore} or a {@link ArchiveStore} that
 * supports queries.
 * <p/>
 * These queries will return their results as a {@link StructuredObject}
 */
public interface QueryableStore {

    /**
     * Allows you to execute a query, in the form of a SQL-like string, against
     * this object
     *
     * @param context
     * @param query
     * @return
     */
    StructuredObject executeQuery(QueryContext context, String query);
}
