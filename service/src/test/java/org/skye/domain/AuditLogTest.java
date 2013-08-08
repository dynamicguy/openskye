package org.skye.domain;

import org.junit.Test;

import java.util.UUID;

import static com.yammer.dropwizard.testing.JsonHelpers.asJson;
import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Test the serialization and handling of the {@link org.skye.domain.Domain}
 */
public class AuditLogTest {

    @Test
    public void serializesToJSON() throws Exception {
        final AuditLog auditLog = new AuditLog();
        //auditLog.setId(UUID.randomUUID().toString());

        auditLog.setId("9c17a5b3-bbbe-4870-90b2-9f1c6378442e");
        assertThat("a AuditLog can be serialized to JSON",
                asJson(auditLog),
                is(equalTo(jsonFixture("fixtures/auditLog.json"))));
    }
}
