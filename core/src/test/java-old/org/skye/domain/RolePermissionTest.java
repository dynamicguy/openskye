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
public class RolePermissionTest {

    @Test
    public void serializesToJSON() throws Exception {
        final RolePermission rolePermission = new RolePermission();
        rolePermission.setId("ac3273e1-1133-4662-85ab-fcc151b56807");
        assertThat("a RolePermission can be serialized to JSON",
                asJson(rolePermission),
                is(equalTo(jsonFixture("fixtures/rolePermission.json"))));
    }
}
