package org.openskye.domain;

/**
 Test the serialization and handling of the {@link Domain}
 */

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class AttributeDefinitionTest extends AbstractDomainTest {
    @Test
    public void serializesToJSON() throws Exception {
        final AttributeDefinition attributeDefinition = new AttributeDefinition();
        attributeDefinition.setId("34ds-5fss-221d-1sdf");
        attributeDefinition.setEmbedInObject(false);
        assertThat("a AttributeDefinition can be serialized to JSON",
                asJson(attributeDefinition),
                is(equalTo(jsonFixture("fixtures/AttributeDefinition.json"))));

    }
}
