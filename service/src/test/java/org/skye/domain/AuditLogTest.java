package org.skye.domain;

import org.junit.Test;

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
        assertThat("a AuditLog can be serialized to JSON",
                asJson(auditLog),
                is(equalTo(jsonFixture("fixtures/auditLog.json"))));
    }
}
