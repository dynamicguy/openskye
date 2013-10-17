package org.openskye.filters;

import org.openskye.core.SkyeException;
import org.openskye.domain.ChannelFilterDefinition;

/**
 * A simple factory for the {@link ChannelFilter} from a {@link org.openskye.domain.ChannelFilterDefinition}
 */
public class ChannelFilterFactory {

    public ChannelFilterFactory() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Returns an instance of a {@link ChannelFilter} for a given {@link ChannelFilterDefinition}
     *
     * @param channelFilterDefinition The filter definition
     * @return an initialized instance
     */
    public static ChannelFilter getInstance(ChannelFilterDefinition channelFilterDefinition) {
        try {
            Class<?> clazz = Class.forName(channelFilterDefinition.getImplementation());
            Object clazzInstance = clazz.newInstance();
            if (clazzInstance instanceof ChannelFilter) {
                ChannelFilter channelFilter = (ChannelFilter) clazzInstance;
                channelFilter.initialize(channelFilterDefinition);
                return channelFilter;
            } else {
                throw new SkyeException("The implementation of the filter is not a ChannelFilter " + channelFilterDefinition);
            }

        } catch (Exception e) {
            throw new SkyeException("Unable to build the implementation for the channel filter " + channelFilterDefinition, e);
        }
    }
}
