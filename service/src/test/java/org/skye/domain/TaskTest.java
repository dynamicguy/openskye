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
public class TaskTest {

    @Test
    public void serializesToJSON() throws Exception {
        final Task task = new Task();
        task.setId("134de17b-4053-465e-8b00-94bd751eb513");
        assertThat("a Task can be serialized to JSON",
                asJson(task),
                is(equalTo(jsonFixture("fixtures/task.json"))));
    }
}
