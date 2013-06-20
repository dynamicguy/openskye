package com.aimtechpartners.skye.platform;

import java.util.UUID;

/**
 * Represents the interface for an <code>ArchiveStore</code>.
 * <p/>
 * An <code>ArchiveStore</code> provides the ability to take a type of {@link SimpleObject} in a {@link Slice} and
 * archive them,  it will provide only the ability to add {@link SimpleObject} within a {@link Slice} to the
 * store
 */
public interface ArchiveStore<T extends SimpleObject> {

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
     * Puts a slice into the archive
     *
     * @param slice the slice to add to the archive
     */
    void putSlice(Slice<T> slice);

    /**
     * Retreives a slice from the archive based on the UUID
     * of the slice that you want
     *
     * @param sliceId the id of the slice to retrieve
     */
    void getSlice(UUID sliceId);
}
