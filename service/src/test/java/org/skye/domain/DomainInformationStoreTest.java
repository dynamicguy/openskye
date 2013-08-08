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
public class DomainInformationStoreTest {

    @Test
    public void serializesToJSON() throws Exception {
        final DomainInformationStore domainInformationStore = new DomainInformationStore();
        assertThat("a Domain can be serialized to JSON",
                asJson(domainInformationStore),
                is(equalTo(jsonFixture("fixtures/domainInformationStore.json"))));
    }
}
