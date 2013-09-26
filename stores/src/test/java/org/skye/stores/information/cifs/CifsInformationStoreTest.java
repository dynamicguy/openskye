package org.skye.stores.information.cifs;

import com.google.guiceberry.junit4.GuiceBerryRule;
import org.junit.Rule;
import org.junit.Test;
import org.skye.domain.*;
import org.skye.stores.StoreRegistry;
import org.skye.stores.archive.localfs.LocalFSArchiveStore;
import org.skye.stores.information.InMemoryTestModule;
import org.skye.stores.information.localfs.LocalFSInformationStore;
import org.skye.stores.inmemory.InMemoryArchiveStore;
import org.skye.task.TaskManager;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: mftaylor
 * Date: 9/23/13
 * Time: 3:36 PM
 * Test the ability to crawl and read files from a CIFS file system
 */
public class CifsInformationStoreTest {

    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(InMemoryTestModule.class);
    @Inject
    public TaskManager taskManager;
    @Inject
    public StoreRegistry registry;

    public InformationStoreDefinition getCifsDis() {
        InformationStoreDefinition dis = new InformationStoreDefinition();
        dis.setImplementation(CifsInformationStore.IMPLEMENTATION);
        // The test CIFS drive below is only available within InfoBelt internal network
        dis.getProperties().put(CifsInformationStore.DOMAIN, "workgroup");
        dis.getProperties().put(CifsInformationStore.PASSWORD, "infobelt12!");
        dis.getProperties().put(CifsInformationStore.USER, "ibtest");
        dis.getProperties().put(CifsInformationStore.HOST, "atpblade08.infobelt.com");
        dis.getProperties().put(CifsInformationStore.PORT, "445");
        dis.getProperties().put(CifsInformationStore.SHARE, "ibshare");
        dis.getProperties().put(CifsInformationStore.NAME, "Test CIFS Drive");
        return dis;
    }

    @Test
    public void createInformationStore() {
        assertThat("We can create an instance of the CIFS information store", registry.build(getCifsDis()).isPresent());
    }

    // This won't work on Travis, so don't annotate with @Test
    public void ensureWeCanDiscoverObjects() {
        assertThat("Get metadata for the store", registry.build(getCifsDis()).get().getMetadata() != null);
        CifsInformationStore is = (CifsInformationStore) registry.build(getCifsDis()).get();

        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        asi.setImplementation(LocalFSArchiveStore.IMPLEMENTATION);
        asi.getProperties().put(LocalFSArchiveStore.LOCALFS_PATH, "/tmp/pj3");
        InformationStoreDefinition dis = getCifsDis();
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

}