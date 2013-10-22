package org.openskye.domain;

/**
 Test the serialization and handling of the {@link Domain}
 */
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ChannelFilterDefinitionTest extends AbstractDomainTest {
    @Test
    public void serializeToJSON() throws Exception
    {
        final ChannelFilterDefinition channelFilterDefinition=new ChannelFilterDefinition();
        channelFilterDefinition.setId("23sw-43er-72yt-67ts");
        channelFilterDefinition.setInclude(true);
        assertThat("a ChannelFilterDefinition can be serialized to JSON",
                asJson(channelFilterDefinition),
                is(equalTo(jsonFixture("fixtures/ChannelFilterDefinition.json"))));




    }

}
