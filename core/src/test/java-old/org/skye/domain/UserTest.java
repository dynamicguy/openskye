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
public class UserTest {

    @Test
    public void serializesToJSON() throws Exception {
        final User user = new User();
        user.setId("c324e70b-1873-4689-a13f-506571d473d3");
        user.setName("Example");
        assertThat("a User can be serialized to JSON",
                asJson(user),
                is(equalTo(jsonFixture("fixtures/user.json"))));
    }
}
