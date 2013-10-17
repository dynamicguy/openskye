package org.openskye.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Test the serialization and handling of the {@link org.openskye.domain.Domain}
 */
public class UserTest extends AbstractDomainTest {

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
