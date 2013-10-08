package org.skye.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Test the serialization and handling of the {@link org.skye.domain.Domain}
 */
public class RolePermissionTest extends AbstractDomainTest {

    @Test
    public void serializesToJSON() throws Exception {
        final RolePermission rolePermission = new RolePermission();
        rolePermission.setId("ac3273e1-1133-4662-85ab-fcc151b56807");
        assertThat("a RolePermission can be serialized to JSON",
                asJson(rolePermission),
                is(equalTo(jsonFixture("fixtures/rolePermission.json"))));
    }
}
