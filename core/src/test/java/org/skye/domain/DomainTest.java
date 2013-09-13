package org.skye.domain;

import org.junit.Test;

import static com.yammer.dropwizard.testing.JsonHelpers.asJson;
import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Test the serialization and handling of the {@link Domain}
 */
public class DomainTest {

    @Test
    public void serializesToJSON() throws Exception {
        final Domain domain = new Domain();
        domain.setId("f344b0b0-01dc-47f8-a885-a0a308e1cf9c");
        domain.setName("Example");
        assertThat("a Domain can be serialized to JSON",
                asJson(domain),
                is(equalTo(jsonFixture("fixtures/domain.json"))));
    }
}
