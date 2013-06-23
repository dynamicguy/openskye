package com.infobelt.skye.platform;

import org.joda.time.DateTime;

import java.util.UUID;

/**
 * A <code>Slice</code> is a representation of a collection of {@link SimpleObject} from an {@link InformationStore} that
 * are to be archived as a logical unit.
 * <p/>
 * A slice
 */
public interface Slice<T extends SimpleObject> {

    /**
     * Returns the original source {@link InformationStore}
     *
     * @return source {@link InformationStore}
     */
    public InformationStore<T> getSourceInformationStore();

    /**
     * Returns the date/time when the <code>Slice</code> was created
     *
     * @return The creation date/time
     */
    public DateTime getDateTime();

    /**
     * Returns the unique id of the Slice
     *
     * @return the id of the slice
     */
    public UUID getId();

    /**
     * Gets an iterator to the {@link SimpleObject} that are within this
     * slice
     *
     * @return iterator to {@link SimpleObject}
     */
    Iterable<T> getObjects();
}
