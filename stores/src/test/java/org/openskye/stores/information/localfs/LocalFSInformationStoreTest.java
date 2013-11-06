package org.openskye.stores.information.localfs;

import com.google.guiceberry.junit4.GuiceBerryRule;
import org.junit.Rule;
import org.junit.Test;
import org.openskye.domain.*;
import org.openskye.stores.StoreRegistry;
import org.openskye.stores.archive.localfs.LocalFSArchiveStore;
import org.openskye.stores.information.InMemoryTestModule;
import org.openskye.stores.inmemory.InMemoryArchiveStore;
import org.openskye.task.TaskManager;

import javax.inject.Inject;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
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

    public InformationStoreDefinition getLocalFsDis() {
        InformationStoreDefinition dis = new InformationStoreDefinition();
        dis.setId(UUID.randomUUID().toString());
        dis.setImplementation(LocalFSInformationStore.IMPLEMENTATION);
        dis.getProperties().put(LocalFSInformationStore.FILE_PATH, "/opt");
        dis.getProperties().put(LocalFSInformationStore.NAME, "Linux /opt Directory");
        return dis;
    }


    public ArchiveStoreDefinition getInMemoryArchive()
    {
        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        asi.setId(UUID.randomUUID().toString());
        asi.setImplementation(InMemoryArchiveStore.IMPLEMENTATION);

        ArchiveStoreDefinition asd = new ArchiveStoreDefinition();
        asd.setId(UUID.randomUUID().toString());
        asd.setArchiveStoreInstance(asi);

        return asd;
    }

    public ArchiveStoreDefinition getLocalFsArchive(String path)
    {
        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        asi.setId(UUID.randomUUID().toString());
        asi.setImplementation(LocalFSArchiveStore.IMPLEMENTATION);
        asi.getProperties().put(LocalFSArchiveStore.LOCALFS_PATH, path);

        ArchiveStoreDefinition asd = new ArchiveStoreDefinition();
        asd.setId(UUID.randomUUID().toString());
        asd.setArchiveStoreInstance(asi);

        return asd;
    }

    @Test
    public void createInformationStore() {
        assertThat("We can create an instance of the Local FS information store", registry.build(getLocalFsDis()).isPresent());
    }

    @Test
    public void ensureWeCanDiscoverObjects() {
        assertThat("Get metadata for the store", registry.build(getLocalFsDis()).get().getMetadata() != null);
        LocalFSInformationStore is = (LocalFSInformationStore) registry.build(getLocalFsDis()).get();

        InformationStoreDefinition dis = getLocalFsDis();
        ArchiveStoreDefinition das = getLocalFsArchive("/tmp/pj2");
        ChannelArchiveStore cas = new ChannelArchiveStore();
        cas.setArchiveStoreDefinition(das);
        Channel channel = new Channel();
        channel.getChannelArchiveStores().add(cas);
        channel.setInformationStoreDefinition(dis);
        ArchiveStoreInstance asi = das.getArchiveStoreInstance();

        assertThat("the information store definition has a valid id",
                (dis.getId() != null && dis.getId().isEmpty() == false));

        assertThat("the archive store definition has a valid id",
                (das.getId() != null && das.getId().isEmpty() == false));

        assertThat("the archive store instance has a valid id",
                (asi.getId() != null && asi.getId().isEmpty() == false));

        Task newTask = new Task();
        newTask.setChannel(channel);
        newTask.setTaskType(TaskType.DISCOVER);
        taskManager.submit(newTask);

        long objectCount = newTask.getStatistics().getSimpleObjectsDiscovered();
        assertThat("We have discovered " + objectCount + " simple objects", objectCount > 0);
    }

    @Test
    public void ensureWeCanArchiveToALocalFS() {

        InformationStoreDefinition dis = getLocalFsDis();
        ArchiveStoreDefinition das = getInMemoryArchive();
        ChannelArchiveStore cas = new ChannelArchiveStore();
        cas.setArchiveStoreDefinition(das);
        Channel channel = new Channel();
        channel.getChannelArchiveStores().add(cas);
        channel.setInformationStoreDefinition(dis);
        channel.setId(UUID.randomUUID().toString());
        ArchiveStoreInstance asi = das.getArchiveStoreInstance();

        // Test that the objects have valid ids.
        assertThat("the information store definition has a valid id",
                (dis.getId() != null && dis.getId().isEmpty() == false));

        assertThat("the archive store definition has a valid id",
                (das.getId() != null && das.getId().isEmpty() == false));

        assertThat("the archive store instance has a valid id",
                (asi.getId() != null && asi.getId().isEmpty() == false));

        Task newTask = new Task();
        newTask.setId(UUID.randomUUID().toString());
        newTask.setChannel(channel);
        newTask.setTaskType(TaskType.DISCOVER);
        taskManager.submit(newTask);

        Task archiveTask = new Task();
        archiveTask.setId(UUID.randomUUID().toString());
        archiveTask.setChannel(channel);
        archiveTask.setTaskType(TaskType.ARCHIVE);
        taskManager.submit(archiveTask);

        long objectsDiscovered = newTask.getStatistics().getSimpleObjectsDiscovered();
        long objectsIngested = archiveTask.getStatistics().getSimpleObjectsIngested();

        assertThat("We have discovered " + objectsDiscovered + " simple objects", objectsDiscovered > 0);
        assertThat("We have ingested " + objectsIngested + " simple objects", objectsIngested > 0);
        assertThat("We have discovered and ingested the same number of simple objects",
                   objectsDiscovered,
                   is(equalTo(objectsIngested)));

    }


}
