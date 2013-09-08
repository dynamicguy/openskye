package org.skye.filters;

import org.skye.core.ObjectMetadata;
import org.skye.domain.ChannelFilterDefinition;

/**
 * An implementation of a {@link ChannelFilter} that can be used by a {@link org.skye.domain.ChannelFilterDefinition}
 */
public class PathRegExFilter implements ChannelFilter {

    public final static String IMPLEMENTATION = PathRegExFilter.class.getCanonicalName();
    private ChannelFilterDefinition channelFilterDefinition;

    @Override
    public void initialize(ChannelFilterDefinition channelFilterDefinition) {
        this.channelFilterDefinition = channelFilterDefinition;
    }

    @Override
    public boolean isFiltered(ObjectMetadata objectMetadata) {
        return objectMetadata.getPath().matches(channelFilterDefinition.getDefinition());
    }

    @Override
    public boolean isInclude() {
        return this.channelFilterDefinition.isInclude();
    }
}
