package org.openskye.domain.dao;

import com.google.inject.Inject;
import org.openskye.domain.*;
import org.openskye.task.step.ArchiveTaskStep;

/**
 * Test the TaskLogDAO
 */
public class TaskLogDAOTest extends AbstractDAOTestBase<TaskLog> {


    @Inject
    public TaskLogDAO taskLogDAO;

    @Override
    public AbstractPaginatingDAO<TaskLog> getDAO() {
        return taskLogDAO;
    }

    @Override
    public TaskLog getNew() {
        TaskLog taskLog = new TaskLog();
        taskLog.setMessage("Task set");
        Domain domain = new Domain();
        domain.setName("Fishstick");
        InformationStoreDefinition isd = new InformationStoreDefinition();
        isd.setName("Test Def");
        Project project = new Project();
        project.setName("Starship 1");
        project.setDomain(domain);
        Channel channel = new Channel();
        channel.setName("Demo Channel");
        channel.setInformationStoreDefinition(isd);
        channel.setProject(project);
        Node node = new Node();
        TaskStatistics taskStatistics = new TaskStatistics();
        Task task = new ArchiveTaskStep(channel, node).toTask();
        taskStatistics.setTask(task);
        task.setStatistics(taskStatistics);
        taskLog.setTaskId(task.getId());
        return taskLog;
    }

    @Override
    public void update(TaskLog instance) {
        Domain domain = new Domain();
        domain.setName("Fishstick");
        InformationStoreDefinition isd = new InformationStoreDefinition();
        isd.setName("Test Def");
        Project project = new Project();
        project.setName("Starship 1");
        project.setDomain(domain);
        Node node = new Node();

        Channel channel = new Channel();
        channel.setName("Demo Channel");
        channel.setInformationStoreDefinition(isd);
        channel.setProject(project);
        TaskStatistics taskStatistics = new TaskStatistics();
        Task task = new ArchiveTaskStep(channel, node).toTask();
        taskStatistics.setTask(task);
        task.setStatistics(taskStatistics);

        instance.setTaskId(task.getId());
        instance.setMessage("task set");
    }
}
