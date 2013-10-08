package org.skye.domain;

import org.junit.Test;

import static io.dropwizard.testing.JsonHelpers.asJson;
import static io.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Test the serialization and handling of the {@link Domain}
 */
public class UserRoleTest {

    @Test
    public void serializesToJSON() throws Exception {
        final UserRole userRole = new UserRole();
        userRole.setId("23f22cd7-4046-418c-8f02-2fe533f80cd3");
        assertThat("a User can be serialized to JSON",
                asJson(userRole),
                is(equalTo(jsonFixture("fixtures/userRole.json"))));
    }
}
