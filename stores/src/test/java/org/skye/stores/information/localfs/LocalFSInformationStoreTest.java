package org.skye.stores.information.localfs;

import com.google.guiceberry.junit4.GuiceBerryRule;
import org.eobjects.metamodel.UpdateCallback;
import org.eobjects.metamodel.UpdateScript;
import org.eobjects.metamodel.UpdateableDataContext;
import org.eobjects.metamodel.schema.ColumnType;
import org.eobjects.metamodel.schema.Schema;
import org.eobjects.metamodel.schema.Table;
import org.junit.Rule;
import org.junit.Test;
import org.skye.domain.*;
import org.skye.stores.StoreRegistry;
import org.skye.stores.archive.localfs.LocalFSArchiveStore;
import org.skye.stores.information.InMemoryTestModule;
import org.skye.stores.information.jdbc.JDBCStructuredInformationStore;
import org.skye.stores.inmemory.InMemoryArchiveStore;
import org.skye.task.TaskManager;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: mftaylor
 * Date: 9/23/13
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
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
        dis.setImplementation(LocalFSInformationStore.IMPLEMENTATION);
        dis.getProperties().put(LocalFSInformationStore.FILE_PATH, "/opt");
        dis.getProperties().put(LocalFSInformationStore.NAME, "Linux /opt Directory");
        return dis;
    }

    @Test
    public void createInformationStore() {
        assertThat("We can create an instance of the Local FS information store", registry.build(getLocalFsDis()).isPresent());
    }

    @Test
    public void ensureWeCanDiscoverObjects() {
        assertThat("Get metadata for the store", registry.build(getLocalFsDis()).get().getMetadata() != null);
        LocalFSInformationStore is = (LocalFSInformationStore) registry.build(getLocalFsDis()).get();

        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        asi.setImplementation(LocalFSArchiveStore.IMPLEMENTATION);
        asi.getProperties().put(LocalFSArchiveStore.LOCALFS_PATH, "/tmp/pj2");
        InformationStoreDefinition dis = getLocalFsDis();
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

        long objectCount = newTask.getStatistics().getSimpleObjectsDiscovered();
        assertThat("We have discovered "+objectCount+" simple objects", objectCount > 0);
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

        Task newTask = new Task();
        newTask.setChannel(channel);
        newTask.setTaskType(TaskType.DISCOVER);
        taskManager.submit(newTask);

        Task archiveTask = new Task();
        archiveTask.setChannel(channel);
        archiveTask.setTaskType(TaskType.ARCHIVE);
        taskManager.submit(newTask);

    }


}
