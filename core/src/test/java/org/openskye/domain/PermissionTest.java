package org.openskye.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Test the serialization and handling of the {@link org.openskye.domain.Domain}
 */
public class PermissionTest extends AbstractDomainTest {

    @Test
    public void serializesToJSON() throws Exception {
        final Permission permission = new Permission();
        permission.setId("88492d92-0fe3-473a-8c9a-5895f23f013d");
        assertThat("a Permission can be serialized to JSON",
                asJson(permission),
                is(equalTo(jsonFixture("fixtures/permission.json"))));
    }
}
