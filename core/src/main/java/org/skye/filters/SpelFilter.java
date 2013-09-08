package org.skye.filters;

import org.skye.core.SimpleObject;
import org.skye.domain.ChannelFilterDefinition;

/**
 * An implementation of a {@link ChannelFilter} that can be used by a {@link org.skye.domain.ChannelFilterDefinition}
 */
public class SpelFilter implements ChannelFilter {

    public final String IMPLEMENTATION = "SpelFilter";

    @Override
    public void initialize(ChannelFilterDefinition channelFilterDefinition) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isFiltered(SimpleObject simpleObject) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isInclude() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
