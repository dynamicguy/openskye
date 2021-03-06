package org.openskye.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Test the serialization and handling of the {@link org.openskye.domain.Domain}
 */
public class AuditLogTest extends AbstractDomainTest {

    @Test
    public void serializesToJSON() throws Exception {
        final AuditLog auditLog = new AuditLog();
        auditLog.setId("9c17a5b3-bbbe-4870-90b2-9f1c6378442e");
        auditLog.setCreatedAt(null);
        assertThat("a AuditLogDAO can be serialized to JSON",
                asJson(auditLog),
                is(equalTo(jsonFixture("fixtures/auditLog.json"))));
    }
}
