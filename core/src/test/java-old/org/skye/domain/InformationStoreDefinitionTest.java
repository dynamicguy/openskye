package org.skye.domain;

import org.junit.Test;

import static io.dropwizard.testing.JsonHelpers.asJson;
import static io.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Test the serialization and handling of the {@link org.skye.domain.Domain}
 */
public class InformationStoreDefinitionTest {

    @Test
    public void serializesToJSON() throws Exception {
        final InformationStoreDefinition informationStoreDefinition = new InformationStoreDefinition();
        informationStoreDefinition.setId("f344b0b0-01dc-47f8-a885-a0a308e1cf9c");
        assertThat("a Domain can be serialized to JSON",
                asJson(informationStoreDefinition),
                is(equalTo(jsonFixture("fixtures/domainInformationStore.json"))));
    }
}
