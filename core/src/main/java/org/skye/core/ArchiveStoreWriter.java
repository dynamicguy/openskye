package org.skye.core;

/**
 * Provides the ability to write {@link SimpleObject} to an {@link ArchiveStore}
 */
public interface ArchiveStoreWriter {

    /**
     * Will load the @{link SimpleObject} content into the store and then it will update
     * the links to the {@link ArchiveContentBlock}
     *
     * @param simpleObject the object to archive
     * @return The simple object enriched with information such as size, checksum etc
     */
    SimpleObject put(SimpleObject simpleObject);

    /**
     * Closes the resources for this writer
     */
    void close();

}
