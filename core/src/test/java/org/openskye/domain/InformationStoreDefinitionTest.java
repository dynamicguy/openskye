package org.openskye.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Test the serialization and handling of the {@link org.openskye.domain.Domain}
 */
public class InformationStoreDefinitionTest extends AbstractDomainTest {

    @Test
    public void serializesToJSON() throws Exception {
        final InformationStoreDefinition informationStoreDefinition = new InformationStoreDefinition();
        informationStoreDefinition.setId("f344b0b0-01dc-47f8-a885-a0a308e1cf9c");
        assertThat("a Domain can be serialized to JSON",
                asJson(informationStoreDefinition),
                is(equalTo(jsonFixture("fixtures/domainInformationStore.json"))));
    }
}
