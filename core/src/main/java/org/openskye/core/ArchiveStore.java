package org.openskye.core;

import com.google.common.base.Optional;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.Task;

/**
 * Represents the interface for an <code>ArchiveStore</code>.
 */
public interface ArchiveStore {

    /**
     * Used to initialize the archive store
     *
     * @param das the domain archive store
     */
    void initialize(ArchiveStoreDefinition das);

    /**
     * Returns the name of the store
     *
     * @return the name
     */
    String getName();

    /**
     * Returns the implementation
     *
     * @return the implementation
     */
    String getImplementation();

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
    Optional<java.io.InputStream> getStream(ObjectMetadata metadata);

    /**
     * Get the simple object back based on the {@link ObjectMetadata}
     *
     * @param metadata the metadata for the object you want to get back
     * @return optionally the {@link SimpleObject}
     */
    Optional<SimpleObject> getSimpleObject(ObjectMetadata metadata);

    /**
     * Returns a list of the {@link ObjectStreamFilter}s that have been
     * enabled on this archive store
     * <p/>
     * Note that not all archive stores honour the {@link ObjectStreamFilter}
     *
     * @return iterable of the filters
     */
    Iterable<ObjectStreamFilter> getFilters();

    /**
     * Gets the {@link ArchiveStoreDefinition} for this instance.
     *
     * @return The {@link ArchiveStoreDefinition} used to initialize this
     * instance, wrapped in an {@link Optional} wrapper, which will be absent
     * if the instance is not initialized.
     */
    Optional<ArchiveStoreDefinition> getArchiveStoreDefinition();

    /**
     * Destroys the object associated to this {@link ObjectMetadata} from this
     * {@link ArchiveStore}
     *
     * @param om the {@link ObjectMetadata} for the object to destroy
     */
    void destroy(ObjectMetadata om);

    /**
     * Puts an ACB into the archive store
     *
     * @param acb The ACB to put in the store
     * @return The ACB that has been put in the store
     */
    ArchiveContentBlock putAcb(ArchiveContentBlock acb);
}
