package org.skye.domain;

import org.junit.Test;

import java.util.UUID;

import static com.yammer.dropwizard.testing.JsonHelpers.asJson;
import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Test the serialization and handling of the {@link org.skye.domain.Domain}
 */
public class ChannelTest {

    @Test
    public void serializesToJSON() throws Exception {
        final Channel channel = new Channel();
        channel.setId("e1d7d93c-fd77-407a-92cf-63ba8924b1e7");
        channel.setName("Example");
        assertThat("a Channel can be serialized to JSON",
                asJson(channel),
                is(equalTo(jsonFixture("fixtures/channel.json"))));
    }
}
