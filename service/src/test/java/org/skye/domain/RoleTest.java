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
public class RoleTest {

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
