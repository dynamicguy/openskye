package org.skye.domain;

import org.junit.Test;

import java.util.UUID;

import static com.yammer.dropwizard.testing.JsonHelpers.asJson;
import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Test the serialization and handling of the {@link Domain}
 */
public class TaskLogTest {

    @Test
    public void serializesToJSON() throws Exception {
        final TaskLog taskLog = new TaskLog();
        taskLog.setId("290b60f1-5da5-4e08-9012-8ed55ae2467d");
        assertThat("a TaskLog can be serialized to JSON",
                asJson(taskLog),
                is(equalTo(jsonFixture("fixtures/taskLog.json"))));
    }
}
