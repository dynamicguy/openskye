package org.openskye.stores;

import com.google.guiceberry.junit4.GuiceBerryRule;
import org.junit.Rule;
import org.junit.Test;
import org.openskye.domain.*;
import org.openskye.stores.inmemory.InMemoryArchiveStore;
import org.openskye.stores.inmemory.InMemoryInformationStore;
import org.openskye.task.TaskManager;
import org.openskye.task.step.DiscoverTaskStep;

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

        Task discover = new DiscoverTaskStep(channel).toTask();
        taskManager.submit(discover);
    }

}
