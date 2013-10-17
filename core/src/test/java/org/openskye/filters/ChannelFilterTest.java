package org.openskye.filters;


import org.junit.Before;
import org.junit.Test;
import org.openskye.core.ObjectMetadata;
import org.openskye.domain.ChannelFilterDefinition;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Some tests for the {@link ChannelFilter}
 */
public class ChannelFilterTest {

    private ChannelFilterDefinition channelFilterDefinition;

    @Before
    public void setup() {
        channelFilterDefinition = new ChannelFilterDefinition();
        channelFilterDefinition.setImplementation(PathRegExFilter.IMPLEMENTATION);
        channelFilterDefinition.setDefinition("test.*");
    }

    @Test
    public void basicBasicPathRegexMatch() {
        ChannelFilter testFilter = ChannelFilterFactory.getInstance(channelFilterDefinition);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setPath("testone");

        assertThat("object should be filtered", testFilter.isFiltered(objectMetadata));
    }

    @Test
    public void basicBasicPathRegexNoMatch() {
        ChannelFilter testFilter = ChannelFilterFactory.getInstance(channelFilterDefinition);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setPath("ttestone");

        assertThat("object should be filtered", !testFilter.isFiltered(objectMetadata));
    }
}
