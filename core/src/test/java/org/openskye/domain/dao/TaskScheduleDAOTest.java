package org.openskye.domain.dao;

import org.junit.Test;
import org.openskye.domain.Project;
import org.openskye.domain.TaskSchedule;
import org.openskye.task.step.TestTaskStep;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;


/**
 * Testing the {@link org.openskye.domain.dao.TaskDAO}
 */
public class TaskScheduleDAOTest extends AbstractDAOTestBase<TaskSchedule> {

    @Inject
    public TaskScheduleDAO taskScheduleDAO;

    @Override
    public AbstractPaginatingDAO getDAO() {
        return taskScheduleDAO;
    }

    @Override
    public TaskSchedule getNew() {
        Project project = new Project();
        project.setId("2093e8ab-6aab-4421-970d-1c79bc3be427");
        TestTaskStep step = new TestTaskStep(project,2,1,true);
        return step.toTaskSchedule("0 0 2 * * ?");
    }

    @Override
    public void update(TaskSchedule instance) {
        instance.setCronExpression("0 0 3 * * ?");
    }

    @Test
    public void doSerialize() throws Exception {
        TaskSchedule instance = getNew();
        instance.serialize();
        instance.setStep(null);
        instance.deserialize();
        assertThat("a TaskSchedule can be serialized and deserialized",
                asJson(instance),
                is(equalTo(jsonFixture("fixtures/taskScheduleSerialize.json"))));
    }
}
