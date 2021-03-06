package org.openskye.core;

import com.google.common.base.Optional;
import org.joda.time.DateTime;
import org.openskye.domain.InformationStoreDefinition;

import java.util.Properties;

/**
 * A representation of an Information Store
 * <p/>
 * An information store is basically any source system that is capable of providing access
 * to a {@link SimpleObject} for ingestion into the Skye ILM framework
 */
public interface InformationStore {

    String NAME = "name";

    /**
     * Used to initialize the information store
     *
     * @param dis The domain information store
     */
    void initialize(InformationStoreDefinition dis);

    /**
     * Returns a set of metadata for this information store
     * <p/>
     * Typically this might include version details for the information store or details on the physical location etc
     *
     * @return a set of properties
     */
    Properties getMetadata();

    /**
     * Returns the name of this instance of the information store, this is a universal name
     * and shouldn't pick up things from the information store definition (it might not be initialized)
     *
     * @return name
     */
    String getName();

    /**
     * Returns the name of the implementation
     *
     * @return implementation
     */
    String getImplementation();

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
    Iterable<SimpleObject> getSince(DateTime dateTime);

    /**
     * Return an iterator to all the root level simple objects within this information store
     *
     * @return iterator to all root simple objects
     */
    Iterable<SimpleObject> getRoot();

    /**
     * Returns an iterator to the child simple objects for a given simple object
     *
     * @param simpleObject the simple object who's children we wish to find
     * @return An iterator to the children
     */
    Iterable<SimpleObject> getChildren(SimpleObject simpleObject);

    /**
     * Returns the related simple objects for the provided simple object within this information store
     *
     * @param simpleObject the simple object who's related simple objects you wish to find
     * @return An iterator for the related simple objects
     */
    Iterable<SimpleObject> getRelated(SimpleObject simpleObject);

    /**
     * Give the implementation string the InformationStore returns true if it implements
     * this
     *
     * @param implementation The string representation of the implementation (ie. JDBC)
     * @return true if the store can implement this implementation
     */
    boolean isImplementing(String implementation);

    /**
     * Used by the information store to take {@link ObjectMetadata} and materialize
     * a simple object that represents this object in that information store,  including
     * the ability to access its information
     *
     * @param objectMetadata the object that needs to be materialized
     * @return the simple object having been materialized
     */
    SimpleObject materialize(ObjectMetadata objectMetadata) throws InvalidSimpleObjectException;

    /**
     * Gets the {@link InformationStoreDefinition} used to initialize this
     * instance.
     *
     * @return An {@link Optional} wrapper which will include the
     *         {@link InformationStoreDefinition} if the instance has been initialized.
     */
    Optional<InformationStoreDefinition> getInformationStoreDefinition();

    /**
     * This method allows you to put a simple object back onto an {@link InformationStore}
     * <p/>
     * Used when an extract has an {@link InformationStore} as a target to extract the simple objects from archive
     * and back to the {@link InformationStore}
     *
     * @param simpleObject the simple object that you want to put back into the {@link InformationStore}
     */
    void put(SimpleObject simpleObject);

    boolean isObjectInStore(SimpleObject simpleObject);
}
