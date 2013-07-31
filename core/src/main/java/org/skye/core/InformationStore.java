package org.skye.core;

import org.joda.time.DateTime;

import java.util.Properties;

/**
 * A representation of an Information Store
 * <p/>
 * An information store is basically any source system that is capable of providing access
 * to a type of {@link SimpleObject} for ingestion into the Skye ILM framework
 */
public interface InformationStore<T extends SimpleObject> {

    /**
     * Used to initialize the information store
     *
     * @param properties the properties used to initialize
     */
    void initialize(Properties properties);

    /**
     * Returns a set of metadata for this information store
     * <p/>
     * Typically this might include version details for the information store or details on the physical location etc
     *
     * @return a set of properties
     */
    Properties getMetadata();

    /**
     * Returns the name of this instance of the information store
     *
     * @return name
     */
    String getName();

    /**
     * Returns the URL identifying this instance of the information store
     *
     * @return the URL
     */
    String getUrl();

    /**
     * Returns an iterable set of the simple objects that have been changed within this information
     * store since the datetime provided
     *
     * @param dateTime the date/time to use to determine changed since
     * @return An iterator to the simple objects that have changed
     */
    Iterable<T> getSince(DateTime dateTime);

    /**
     * Return an iterator to all the root level simple objects within this information store
     *
     * @return iterator to all root simple objects
     */
    Iterable<T> getAll();

    /**
     * Returns an iterator to the child simple objects for a given simple object
     *
     * @param simpleObject the simple object who's children we wish to find
     * @return An iterator to the children
     */
    Iterable<T> getChildren(T simpleObject);

    /**
     * Returns the related simple objects for the provided simple object within this information store
     *
     * @param simpleObject the simple object who's related simple objects you wish to find
     * @return An iterator for the related simple objects
     */
    Iterable<T> getRelated(T simpleObject);

}
