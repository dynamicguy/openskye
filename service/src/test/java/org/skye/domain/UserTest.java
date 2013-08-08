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
public class UserTest {

    @Test
    public void serializesToJSON() throws Exception {
        final User user = new User();
        user.setName("Example");
        assertThat("a User can be serialized to JSON",
                asJson(user),
                is(equalTo(jsonFixture("fixtures/user.json"))));
    }
}
