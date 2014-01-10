package org.openskye.stores;

import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.persist.PersistService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openskye.domain.*;
import org.openskye.guice.InMemoryTestModule;
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
    @Inject
    PersistService persistService;

    @Before
    public void checkStarted() {
        try {
            persistService.start();
        } catch (IllegalStateException e) {
            // Ignore it we are started
        }
    }

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
        Project project = new Project();
        project.setName("Test Project");
        project.setId("f86c8b63-fd3b-4c7b-ae96-5f87ff53a187");
        channel.setProject(project);

        Node node = new Node();
        Task discover = new DiscoverTaskStep(channel, node).toTask();
        taskManager.submit(discover);
    }

}
