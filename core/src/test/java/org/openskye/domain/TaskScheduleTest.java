package org.openskye.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Test the serialization and handling of the {@link TaskSchedule}
 */
public class TaskScheduleTest extends AbstractDomainTest {

    @Test
    public void serializesToJSON() throws Exception {
        final TaskSchedule taskSchedule = new TaskSchedule();
        taskSchedule.setId("290b60f1-5da5-4e08-9012-8ed55ae2467d");
        assertThat("a TaskSchedule can be serialized to JSON",
                asJson(taskSchedule),
                is(equalTo(jsonFixture("fixtures/TaskSchedule.json"))));
    }
}
