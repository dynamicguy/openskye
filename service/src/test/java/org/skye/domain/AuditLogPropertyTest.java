package org.skye.domain;

import org.junit.Test;

import static com.yammer.dropwizard.testing.JsonHelpers.asJson;
import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Test the serialization and handling of the {@link Domain}
 */
public class AuditLogPropertyTest {

    @Test
    public void serializesToJSON() throws Exception {
        final AuditLogProperty auditLogProperty = new AuditLogProperty();
        auditLogProperty.setPropertyValue("test");
        auditLogProperty.setPropertyName("Example");
        assertThat("a AuditLogProperty can be serialized to JSON",
                asJson(auditLogProperty),
                is(equalTo(jsonFixture("fixtures/auditLogProperty.json"))));
    }
}
