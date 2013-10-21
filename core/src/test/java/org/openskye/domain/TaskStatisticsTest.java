package org.openskye.domain;

/**
 * Created with IntelliJ IDEA.
 * User: Krishna
 * Date: 10/21/13
 * Time: 1:13 PM
 * To change this template use File | Settings | File Templates.
 */
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class TaskStatisticsTest extends AbstractDomainTest{
    @Test
    public void serializeToJSON() throws Exception
    {
        final TaskStatistics taskStatistics=new TaskStatistics();
        taskStatistics.setId("63dd-69ow-39zx-40kl");
        taskStatistics.setSimpleObjectsIngested(0);
        taskStatistics.setSimpleObjectsDiscovered(0);
        assertThat("a TaskStatistics can be serialized to JSON",
                asJson(taskStatistics),
                is(equalTo(jsonFixture("fixtures/TaskStatistics.json"))));

    }
}
