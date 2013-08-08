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
public class TaskLogTest {

    @Test
    public void serializesToJSON() throws Exception {
        final TaskLog taskLog = new TaskLog();
        assertThat("a TaskLog can be serialized to JSON",
                asJson(taskLog),
                is(equalTo(jsonFixture("fixtures/taskLog.json"))));
    }
}
