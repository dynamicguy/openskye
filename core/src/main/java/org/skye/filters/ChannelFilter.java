package org.skye.filters;

import org.skye.core.SimpleObject;
import org.skye.domain.ChannelFilterDefinition;

/**
 * The interface that an instance of a {@link org.skye.domain.ChannelFilterDefinition}
 * needs to implement
 */
public interface ChannelFilter {

    /**
     * Before a <code>ChannelFilter</code> can be used you must initialize it
     * using the <code>ChannelFilterDefinition</code>
     *
     * @param channelFilterDefinition The definition used to initialize this instance
     */
    void initialize(ChannelFilterDefinition channelFilterDefinition);

    /**
     * A method to test a {@link SimpleObject},  it will return true if the SimpleObject was filtered
     *
     * @param simpleObject
     * @return
     */
    boolean isFiltered(SimpleObject simpleObject);

    /**
     * Returns true if the filter is including simple objects based on the filter,  if it is false
     * then the filter is excluding simple objects based on the filter
     *
     * @return true if including
     */
    boolean isInclude();
}
