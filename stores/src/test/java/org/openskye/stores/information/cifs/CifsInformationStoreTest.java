package org.openskye.stores.information.cifs;

import com.google.guiceberry.junit4.GuiceBerryRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.openskye.domain.*;
import org.openskye.stores.StoreRegistry;
import org.openskye.stores.archive.localfs.LocalFSArchiveStore;
import org.openskye.stores.information.InMemoryTestModule;
import org.openskye.task.TaskManager;
import org.openskye.task.step.DiscoverTaskStep;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;

/**
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
    @Test
    @Ignore
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

        Task discover = new DiscoverTaskStep(channel).toTask();
        taskManager.submit(discover);

        long objectCount = discover.getStatistics().getSimpleObjectsDiscovered();
        assertThat("We have discovered " + objectCount + " simple objects", objectCount > 0);
    }

}
