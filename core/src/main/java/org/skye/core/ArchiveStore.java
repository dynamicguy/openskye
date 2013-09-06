package org.skye.core;

import com.google.common.base.Optional;
import org.omg.CORBA.portable.InputStream;
import org.skye.domain.DomainArchiveStore;
import org.skye.domain.Task;

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
     * Returns an instance of an {@link ArchiveStoreWriter} for this given task
     *
     * @param task the task to create the writer for
     * @return An instance of an archive store writer
     */
    ArchiveStoreWriter getWriter(Task task);

    /**
     * Will get a stream of the content of the @{link ObjectMetadata}
     *
     * @param metadata the object to get a stream to
     */
    InputStream getStream(ObjectMetadata metadata);

    /**
     * Get the simple object back based on the {@link ObjectMetadata}
     *
     * @param metadata the metadata for the object you want to get back
     * @return optionally the {@link SimpleObject}
     */
    Optional<SimpleObject> getSimpleObject(ObjectMetadata metadata);


}
