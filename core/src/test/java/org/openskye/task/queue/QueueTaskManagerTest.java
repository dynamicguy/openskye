package org.openskye.task.queue;

import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.persist.PersistService;
import org.junit.*;
import org.openskye.core.SkyeException;
import org.openskye.domain.Project;
import org.openskye.domain.Task;
import org.openskye.domain.TaskStatus;
import org.openskye.domain.dao.TaskDAO;
import org.openskye.task.TaskManager;
import org.openskye.task.step.TestTaskStep;

import javax.inject.Inject;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test the basic functions of the QueueTaskManager
 */
public class QueueTaskManagerTest {
    @Rule
    public final GuiceBerryRule guiceBerryRule = new GuiceBerryRule(QueueTaskManagerTestModule.class);
    @Inject
    public TaskManager taskManager;
    @Inject
    public TaskDAO tasks;
    @Inject
    PersistService persistService;

    private void checkStatus(Task task,TaskStatus status) throws Exception {
        assertThat("Task status should be "+status+", was "+task.getStatus(),
                task.getStatus() == status);
    }

    @Before
    public void checkStarted() {
        try {
            persistService.start();
        } catch( IllegalStateException ie ) {
            // not a problem -- just tries to initialize JPA twice
        }
        taskManager.start();
    }

    @Test
    public void doTestTaskPass() throws Exception {
        QueueTaskManager queueTaskManager = (QueueTaskManager) taskManager;
        Project mockProject = new Project();
        mockProject.setId(UUID.randomUUID().toString());
        boolean pass = true;
        Task testTask = new TestTaskStep(mockProject,0,0,pass).toTask();
        testTask.setWorkerName("Skye Worker");
        tasks.create(testTask);
        checkStatus(testTask,TaskStatus.CREATED);
        queueTaskManager.submit(testTask);
        checkStatus(testTask, TaskStatus.QUEUED);
        queueTaskManager.accept(testTask.getId(),"Skye Worker");
        checkStatus(testTask,TaskStatus.STARTED);
        TaskStatus finalStatus = testTask.getStep().call();
        queueTaskManager.end(testTask.getId(),finalStatus,null);
        checkStatus(testTask,TaskStatus.COMPLETED);
    }

    @Test
    public void doTestTaskFail() throws Exception {
        QueueTaskManager queueTaskManager = (QueueTaskManager) taskManager;
        Project mockProject = new Project();
        mockProject.setId(UUID.randomUUID().toString());
        boolean pass = false;
        Task testTask = new TestTaskStep(mockProject,0,0,pass).toTask();
        testTask.setWorkerName("Skye Worker");
        tasks.create(testTask);
        checkStatus(testTask,TaskStatus.CREATED);
        queueTaskManager.submit(testTask);
        checkStatus(testTask,TaskStatus.QUEUED);
        queueTaskManager.accept(testTask.getId(),"Skye Worker");
        checkStatus(testTask,TaskStatus.STARTED);
        TaskStatus finalStatus;
        Exception exception = null;
        try {
            finalStatus = testTask.getStep().call();
        } catch( SkyeException e ) {
            finalStatus = TaskStatus.FAILED;
            exception = e;
        }
        queueTaskManager.end(testTask.getId(),finalStatus,exception);
        checkStatus(testTask,TaskStatus.FAILED);
    }

    @Test(expected = SkyeException.class)
    public void doWrongWorker() throws Exception {
        QueueTaskManager queueTaskManager = (QueueTaskManager) taskManager;
        Project mockProject = new Project();
        mockProject.setId(UUID.randomUUID().toString());
        boolean pass = true;
        Task testTask = new TestTaskStep(mockProject,0,0,pass).toTask();
        testTask.setWorkerName("Some Other Worker");
        tasks.create(testTask);
        checkStatus(testTask,TaskStatus.CREATED);
        queueTaskManager.submit(testTask);
        checkStatus(testTask, TaskStatus.QUEUED);
        queueTaskManager.accept(testTask.getId(),"Skye Worker");
    }
}

