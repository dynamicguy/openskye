package org.openskye.stores.archive.jdbc;

import com.google.common.base.Optional;
import org.openskye.core.*;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.Task;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: joshua
 * Date: 10/22/13
 * Time: 10:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class JDBCStructuredArchiveStore implements ArchiveStore
{
    public ArchiveStoreDefinition definition;

    /**
     * Used to initialize the archive store
     *
     * @param das the domain archive store
     */
    @Override
    public void initialize(ArchiveStoreDefinition das)
    {
        this.definition = das;
    }

    /**
     * Returns the name of the store
     *
     * @return the name
     */
    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns the URL of the store
     *
     * @return the url
     */
    @Override
    public String getUrl() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Determines if the ArchiveStore is able to support this {@link org.openskye.core.SimpleObject}
     *
     * @param so the simple object to archive
     * @return true if this {@link org.openskye.core.ArchiveStore} supports this {@link org.openskye.core.SimpleObject}
     */
    @Override
    public boolean isSupported(SimpleObject so) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Give the implementation string the ArchiveStore returns true if it implements
     * this
     *
     * @param implementation The string representation of the implementation (ie. LocalFS)
     * @return true if the store can implement this implementation
     */
    @Override
    public boolean isImplementing(String implementation) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns an instance of an {@link org.openskye.core.ArchiveStoreWriter} for this given task
     *
     * @param task the task to create the writer for
     * @return An instance of an archive store writer
     */
    @Override
    public ArchiveStoreWriter getWriter(Task task) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Will get a stream of the content of the @{link ObjectMetadata}
     *
     * @param metadata the object to get a stream to
     */
    @Override
    public Optional<InputStream> getStream(ObjectMetadata metadata) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Get the simple object back based on the {@link org.openskye.core.ObjectMetadata}
     *
     * @param metadata the metadata for the object you want to get back
     * @return optionally the {@link org.openskye.core.SimpleObject}
     */
    @Override
    public Optional<SimpleObject> getSimpleObject(ObjectMetadata metadata) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns a list of the {@link org.openskye.core.ObjectStreamFilter}s that have been
     * enabled on this archive store
     * <p/>
     * Note that not all archive stores honour the {@link org.openskye.core.ObjectStreamFilter}
     *
     * @return iterable of the filters
     */
    @Override
    public Iterable<ObjectStreamFilter> getFilters() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Gets the {@link org.openskye.domain.ArchiveStoreDefinition} for this instance.
     *
     * @return The {@link org.openskye.domain.ArchiveStoreDefinition} used to initialize this
     *         instance, wrapped in an {@link com.google.common.base.Optional} wrapper, which will be absent
     *         if the instance is not initialized.
     */
    @Override
    public Optional<ArchiveStoreDefinition> getArchiveStoreDefinition() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Destroys the object associated to this {@link org.openskye.core.ObjectMetadata} from this
     * {@link org.openskye.core.ArchiveStore}
     *
     * @param om the {@link org.openskye.core.ObjectMetadata} for the object to destroy
     */
    @Override
    public void destroy(ObjectMetadata om) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
