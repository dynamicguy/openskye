package org.openskye.stores.archive.jdbc;

import org.openskye.core.SimpleObject;
import org.openskye.stores.archive.AbstractArchiveStoreWriter;

/**
 * Created with IntelliJ IDEA.
 * User: joshua
 * Date: 10/22/13
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class JDBCStructuredArchiveStoreWriter extends AbstractArchiveStoreWriter
{
    /**
     * Will load the @{link SimpleObject} content into the store and then it will update
     * the links to the {@link org.openskye.core.ArchiveContentBlock}
     *
     * @param simpleObject the object to archive
     * @return The simple object enriched with information such as size, checksum etc
     */
    @Override
    public SimpleObject put(SimpleObject simpleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Closes the resources for this writer
     */
    @Override
    public void close() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
