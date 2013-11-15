package org.openskye.domain.dao;

import com.google.inject.Inject;
import org.openskye.domain.*;
import org.openskye.task.step.ArchiveTaskStep;

/**
 Test the TaskStatisticsDAO
 */
public class TaskStatisticsDAOTest  extends AbstractDAOTestBase<TaskStatistics>{

    @Inject
    public TaskStatisticsDAO taskStatisticsDAO;
    @Override
    public AbstractPaginatingDAO<TaskStatistics> getDAO() {
        return taskStatisticsDAO;
    }

    @Override
    public TaskStatistics getNew() {
        TaskStatistics taskStatistics=new TaskStatistics();
        Domain domain=new Domain();
        domain.setName("Fishstick");
        InformationStoreDefinition isd = new InformationStoreDefinition();
        isd.setName("Test Def");
        Project project = new Project();
        project.setName("Starship 1");
        project.setDomain(domain);
        Channel channel=new Channel();
        channel.setName("Demo Channel");
        channel.setInformationStoreDefinition(isd);
        channel.setProject(project);
        Task task=new ArchiveTaskStep(channel).toTask();
        taskStatistics.setTask(task);
        task.setStatistics(taskStatistics);
        taskStatistics.setTask(task);
        taskStatistics.setSimpleObjectsIngested(3453324);
        taskStatistics.setSimpleObjectsDiscovered(543345);
        return taskStatistics;
    }

    @Override
    public void update(TaskStatistics instance) {
        TaskStatistics taskStatistics=new TaskStatistics();
        Domain domain=new Domain();
        domain.setName("Fishstick");
        InformationStoreDefinition isd = new InformationStoreDefinition();
        isd.setName("Test Def");
        Project project = new Project();
        project.setName("Starship 1");
        project.setDomain(domain);
        Channel channel=new Channel();
        channel.setName("Demo Channel");
        channel.setInformationStoreDefinition(isd);
        channel.setProject(project);
        Task task=new ArchiveTaskStep(channel).toTask();
        taskStatistics.setTask(task);
        task.setStatistics(taskStatistics);
        instance.setTask(task);
        instance.setSimpleObjectsDiscovered(243324);
        instance.setSimpleObjectsIngested(233342);
    }
}
