package org.skye.stores;

import com.google.guiceberry.junit4.GuiceBerryRule;
import org.junit.Rule;
import org.junit.Test;
import org.skye.domain.*;
import org.skye.stores.inmemory.InMemoryArchiveStore;
import org.skye.stores.inmemory.InMemoryInformationStore;
import org.skye.task.TaskManager;

import javax.inject.Inject;

/**
 * A set of basic tests for the registry
 */
public class InMemoryArchiveTest {

    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(InMemoryTestModule.class);
    @Inject
    public TaskManager taskManager;

    @Test
    public void testBasicArchiving() throws Exception {
        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        asi.setImplementation(InMemoryArchiveStore.IMPLEMENTATION);
        InformationStoreDefinition dis = new InformationStoreDefinition();
        dis.setImplementation(InMemoryInformationStore.IMPLEMENTATION);
        ArchiveStoreDefinition das = new ArchiveStoreDefinition();
        das.setArchiveStoreInstance(asi);
        ChannelArchiveStore cas = new ChannelArchiveStore();
        cas.setArchiveStoreDefinition(das);
        Channel channel = new Channel();
        channel.getChannelArchiveStores().add(cas);
        channel.setInformationStoreDefinition(dis);

        Task newTask = new Task();
        newTask.setChannel(channel);
        newTask.setTaskType(TaskType.DISCOVER);
        taskManager.submit(newTask);
    }

}
