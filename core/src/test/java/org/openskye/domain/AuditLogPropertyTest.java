package org.openskye.domain;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Test the serialization and handling of the {@link Domain}
 */
public class AuditLogPropertyTest extends AbstractDomainTest {

    @Test
    public void serializesToJSON() throws Exception {
        final AuditLogProperty auditLogProperty = new AuditLogProperty();
        auditLogProperty.setId("bea79eb6-54cd-46ba-ab33-865511b2f951");
        auditLogProperty.setPropertyValue("test");
        auditLogProperty.setPropertyName("Example");
        assertThat("a AuditLogProperty can be serialized to JSON",
                asJson(auditLogProperty),
                is(equalTo(jsonFixture("fixtures/auditLogProperty.json"))));
    }


}
