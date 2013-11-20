package org.openskye.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Test the serialization and handling of the {@link Domain}
 */
public class TaskLogTest extends AbstractDomainTest {

    @Test
    public void serializesToJSON() throws Exception {
        final TaskLog taskLog = new TaskLog();
        taskLog.setId("290b60f1-5da5-4e08-9012-8ed55ae2467d");
        taskLog.setLogTime(null);
        assertThat("a TaskLog can be serialized to JSON",
                asJson(taskLog),
                is(equalTo(jsonFixture("fixtures/taskLog.json"))));
    }
}
