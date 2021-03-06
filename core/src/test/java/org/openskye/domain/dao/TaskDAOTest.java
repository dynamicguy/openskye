package org.openskye.domain.dao;

import org.junit.Test;
import org.openskye.domain.Node;
import org.openskye.domain.Project;
import org.openskye.domain.Task;
import org.openskye.task.step.TestTaskStep;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Testing the {@link org.openskye.domain.dao.TaskDAO}
 */
public class TaskDAOTest extends AbstractDAOTestBase<Task> {

    @Inject
    public TaskDAO taskDAO;

    @Override
    public AbstractPaginatingDAO getDAO() {
        return taskDAO;
    }

    @Override
    public Task getNew() {
        Project project = new Project();
        Node node = new Node();
        project.setId("2093e8ab-6aab-4421-970d-1c79bc3be427");
        TestTaskStep step = new TestTaskStep(project, node, 2, 1, true);
        return step.toTask();
    }

    @Override
    public void update(Task instance) {
        // TODO do something
    }

    @Test
    public void doSerialize() throws Exception {
        Task instance = getNew();
        instance.serialize();
        instance.setStep(null);
        instance.setStepLabel(null);
        instance.deserialize();
        assertThat("a Task can be serialized and deserialized",
                asJson(instance),
                is(equalTo(jsonFixture("fixtures/taskSerialize.json"))));
    }
}
