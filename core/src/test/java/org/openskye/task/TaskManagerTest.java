package org.openskye.task;

import com.google.guiceberry.junit4.GuiceBerryRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openskye.core.SkyeException;
import org.openskye.domain.Node;
import org.openskye.domain.Project;
import org.openskye.domain.Task;
import org.openskye.domain.TaskStatus;
import org.openskye.domain.dao.TaskDAO;
import org.openskye.guice.InMemoryTestModule;
import org.openskye.task.step.TestTaskStep;

import javax.inject.Inject;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This class performs unit testing for the TaskManager
 */
public class TaskManagerTest {
    @Rule
    public final GuiceBerryRule guiceBerryRule = new GuiceBerryRule(InMemoryTestModule.class);
    @Inject
    public TaskManager taskManager;
    @Inject
    public TaskDAO tasks;

    @Before
    public void checkStarted() {
        taskManager.start();
    }

    @Test
    public void doTestTaskPass() throws Exception {
        Project mockProject = new Project();
        Node testNode = new Node();
        mockProject.setId(UUID.randomUUID().toString());
        boolean pass = true;
        Task testTask = new TestTaskStep(mockProject, testNode, 0, 0, pass).toTask();
        assertThat("Task status should be CREATED, was " + testTask.getStatus(),
                testTask.getStatus() == TaskStatus.CREATED);
        taskManager.submit(testTask);
        assertThat("Task status should be COMPLETED, was " + testTask.getStatus(),
                testTask.getStatus() == TaskStatus.COMPLETED);
    }

    @Test(expected = SkyeException.class)
    public void doTestTaskFail() throws Exception {
        Project mockProject = new Project();
        Node testNode = new Node();

        mockProject.setId(UUID.randomUUID().toString());
        boolean pass = false;
        Task testTask = new TestTaskStep(mockProject, testNode, 0, 0, pass).toTask();
        assertThat("Task status should be CREATED, was " + testTask.getStatus(),
                testTask.getStatus() == TaskStatus.CREATED);
        taskManager.submit(testTask);
        assertThat("Task status should be FAILED, was " + testTask.getStatus(),
                testTask.getStatus() == TaskStatus.FAILED);
    }
}

