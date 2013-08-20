package org.skye.core;

import org.skye.domain.DomainArchiveStore;

/**
 * Represents the interface for an <code>ArchiveStore</code>.
 */
public interface ArchiveStore {

    /**
     * Used to initialize the archive store
     *
     * @param das the domain archive store
     */
    void initialize(DomainArchiveStore das);

    /**
     * Returns the name of the store
     *
     * @return the name
     */
    String getName();

    /**
     * Returns the URL of the store
     *
     * @return the url
     */
    String getUrl();

    /**
     * Determines if the ArchiveStore is able to support this {@link SimpleObject}
     *
     * @param so the simple object to archive
     * @return true if this {@link ArchiveStore} supports this {@link SimpleObject}
     */
    boolean isSupported(SimpleObject so);

    /**
     * Give the implementation string the ArchiveStore returns true if it implements
     * this
     *
     * @param implementation The string representation of the implementation (ie. LocalFS)
     * @return true if the store can implement this implementation
     */
    boolean isImplementing(String implementation);

    /**
     * Will load the @{link SimpleObject} content into the store and then it will update
     * the links to the {@link ArchiveContentBlock}
     *
     * @param simpleObject the object to archive
     */
    void put(SimpleObject simpleObject);
}
