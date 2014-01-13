package org.openskye.stores.information.localfs;

import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.persist.PersistService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openskye.domain.*;
import org.openskye.stores.StoreRegistry;
import org.openskye.stores.archive.host.HostArchiveStore;
import org.openskye.stores.information.InMemoryTestModule;
import org.openskye.stores.inmemory.InMemoryArchiveStore;
import org.openskye.task.TaskManager;
import org.openskye.task.step.ArchiveTaskStep;
import org.openskye.task.step.DiscoverTaskStep;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This is a set of tests for the {@link LocalFSInformationStore}
 */
public class LocalFSInformationStoreTest {

    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(InMemoryTestModule.class);
    @Inject
    public TaskManager taskManager;
    @Inject
    public StoreRegistry registry;
    @Inject
    public PersistService persistService;

    @Before
    public void setUp() {
        try {
            persistService.start();
        } catch (IllegalStateException e) {
            // Ignore it we are started
        }
    }

    public InformationStoreDefinition getLocalFsDis() {
        InformationStoreDefinition dis = new InformationStoreDefinition();
        dis.setId(UUID.randomUUID().toString());
        dis.setImplementation(LocalFSInformationStore.IMPLEMENTATION);
        String workingDir = System.getProperty("user.dir");
        dis.getProperties().put(LocalFSInformationStore.FILE_PATH, workingDir);
        dis.getProperties().put(LocalFSInformationStore.NAME, "Current Working Directory");
        return dis;
    }

    @Test
    public void createInformationStore() {
        assertThat("We can create an instance of the Local FS information store", registry.build(getLocalFsDis()).isPresent());
    }

    @Test
    public void ensureWeCanDiscoverObjects() throws Exception {
        assertThat("Get metadata for the store", registry.build(getLocalFsDis()).get().getMetadata() != null);
        LocalFSInformationStore is = (LocalFSInformationStore) registry.build(getLocalFsDis()).get();

        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        asi.setImplementation(HostArchiveStore.IMPLEMENTATION);
        Path temp = Files.createTempDirectory("pj2");
        asi.getProperties().put(HostArchiveStore.FILE_PATH, temp.toAbsolutePath().toString());
        InformationStoreDefinition dis = getLocalFsDis();
        ArchiveStoreDefinition das = new ArchiveStoreDefinition();
        das.setArchiveStoreInstance(asi);
        ChannelArchiveStore cas = new ChannelArchiveStore();
        cas.setArchiveStoreDefinition(das);
        Channel channel = new Channel();
        channel.getChannelArchiveStores().add(cas);
        channel.setInformationStoreDefinition(dis);
        Project project = new Project();
        project.setId(UUID.randomUUID().toString());
        channel.setProject(project);

        Node node = new Node();


        Task discover = new DiscoverTaskStep(channel, node).toTask();
        taskManager.submit(discover);

        long objectCount = discover.getStatistics().getSimpleObjectsFound();
        assertThat("We have discovered " + objectCount + " simple objects", objectCount > 0);
    }

    @Test
    public void ensureWeCanArchiveToALocalFS() {

        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        asi.setImplementation(InMemoryArchiveStore.IMPLEMENTATION);
        InformationStoreDefinition dis = getLocalFsDis();
        ArchiveStoreDefinition das = new ArchiveStoreDefinition();
        das.setArchiveStoreInstance(asi);
        ChannelArchiveStore cas = new ChannelArchiveStore();
        cas.setArchiveStoreDefinition(das);
        Channel channel = new Channel();
        channel.getChannelArchiveStores().add(cas);
        channel.setInformationStoreDefinition(dis);
        Project project = new Project();
        project.setId(UUID.randomUUID().toString());
        channel.setProject(project);
        Node node = new Node();


        Task discover = new DiscoverTaskStep(channel, node).toTask();
        taskManager.submit(discover);

        Task archive = new ArchiveTaskStep(channel, node).toTask();
        taskManager.submit(archive);

    }

}
