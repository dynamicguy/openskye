package org.skye.core;

import java.util.Properties;

/**
 * Represents the interface for an <code>ArchiveStore</code>.
 */
public interface ArchiveStore {

    /**
     * Used to initialize the archive store
     *
     * @param properties the properties used to initialize
     */
    void initialize(Properties properties);

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
}
