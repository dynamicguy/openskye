package org.openskye.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Test the serialization and handling of the {@link Domain}
 */
public class UserRoleTest extends AbstractDomainTest {

    @Test
    public void serializesToJSON() throws Exception {
        final UserRole userRole = new UserRole();
        userRole.setId("23f22cd7-4046-418c-8f02-2fe533f80cd3");
        assertThat("a User can be serialized to JSON",
                asJson(userRole),
                is(equalTo(jsonFixture("fixtures/userRole.json"))));
    }
}
