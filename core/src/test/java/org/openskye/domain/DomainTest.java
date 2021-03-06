package org.openskye.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Test the serialization and handling of the {@link Domain}
 */
public class DomainTest extends AbstractDomainTest {

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
