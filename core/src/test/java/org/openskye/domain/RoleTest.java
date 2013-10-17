package org.openskye.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Test the serialization and handling of the {@link org.openskye.domain.Domain}
 */
public class RoleTest extends AbstractDomainTest {

    @Test
    public void serializesToJSON() throws Exception {
        final Role role = new Role();
        role.setId("8b81bad7-8fcf-4c8e-95b6-e2e03996f376");
        role.setName("Example");
        assertThat("a Role can be serialized to JSON",
                asJson(role),
                is(equalTo(jsonFixture("fixtures/role.json"))));
    }
}
