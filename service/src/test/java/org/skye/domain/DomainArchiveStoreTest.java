package org.skye.domain;

import org.junit.Test;

import static com.yammer.dropwizard.testing.JsonHelpers.asJson;
import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Test the serialization and handling of the {@link org.skye.domain.Domain}
 */
public class DomainArchiveStoreTest {

    @Test
    public void serializesToJSON() throws Exception {
        final DomainArchiveStore domainArchiveStore = new DomainArchiveStore();
        domainArchiveStore.setId("44252357-f7bb-4e77-8d0c-c478e129bf1d");
        assertThat("a DomainArchiveStore can be serialized to JSON",
                asJson(domainArchiveStore),
                is(equalTo(jsonFixture("fixtures/DomainArchiveStore.json"))));
    }
}
