package org.openskye.core;

import java.util.List;

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


    public abstract boolean isObjectArchived(SimpleObject simpleObject);

    /**
     * Compress a {@link SimpleObject} into an UnstructuredCompressedObject.
     *
     * @param so The {@link SimpleObject} to compress
     * @return The resulting compressed object
     */
    public abstract UnstructuredCompressedObject compress(SimpleObject so);

    /**
     * Compress an {@link org.openskye.core.ArchiveContentBlock} into an UnstructuredCompressedObject.
     *
     * @param acb The {@link org.openskye.core.ArchiveContentBlock} to compress
     * @return The resulting compressed object
     */
    public abstract void compress(ArchiveContentBlock acb);

    public abstract void compressAllACBs();

    public abstract List<SimpleObject> decompress(UnstructuredCompressedObject compressedObject);

    /**
     * Closes the resources for this writer
     */
    void close();

}
