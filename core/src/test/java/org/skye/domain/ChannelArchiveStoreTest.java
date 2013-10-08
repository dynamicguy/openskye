package org.skye.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Test the serialization and handling of the {@link org.skye.domain.Domain}
 */
public class ChannelArchiveStoreTest extends AbstractDomainTest {

    @Test
    public void serializesToJSON() throws Exception {
        final ChannelArchiveStore channelArchiveStore = new ChannelArchiveStore();
        channelArchiveStore.setId("8b206d2e-6cf2-432c-8d8e-a0e49fd3393c");
        assertThat("a ChannelArchiveStore can be serialized to JSON",
                asJson(channelArchiveStore),
                is(equalTo(jsonFixture("fixtures/ChannelArchiveStore.json"))));
    }
}
