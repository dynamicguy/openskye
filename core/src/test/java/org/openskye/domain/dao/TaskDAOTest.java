package org.openskye.domain.dao;

import com.google.inject.Inject;
import org.openskye.domain.*;

/**
Test the TaskDAO
 */
public class TaskDAOTest extends AbstractDAOTestBase<Task> {

    @Inject
    public TaskDAO taskDAO;


    @Override
    public AbstractPaginatingDAO<Task> getDAO() {
        return taskDAO;
    }

    @Override
    public Task getNew() {
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
        TaskStatistics taskStatistics=new TaskStatistics();
        Task task=new Task();
        taskStatistics.setTask(task);
        task.setChannel(channel);
        task.setProject(project);
        task.setTargetInformationStoreDefinition(isd);
        task.setStatistics(taskStatistics);
        return task;
    }

    @Override
    public void update(Task instance) {
        Domain domain=new Domain();
        TaskStatistics taskStatistics=new TaskStatistics();
        Task task=new Task();
        taskStatistics.setTask(task);
        instance.setStatistics(taskStatistics);
        InformationStoreDefinition isd = new InformationStoreDefinition();
        isd.setName("Test Def");
        instance.setTargetInformationStoreDefinition(isd);
        Project project = new Project();
        project.setName("Starship 1");
        project.setDomain(domain);
        instance.setProject(project);
        Channel channel=new Channel();
        channel.setName("Demo Channel");
        channel.setInformationStoreDefinition(isd);
        channel.setProject(project);
        instance.setChannel(channel);

    }


}
