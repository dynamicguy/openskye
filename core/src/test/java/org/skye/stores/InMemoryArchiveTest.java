package org.skye.stores;

import com.google.guiceberry.junit4.GuiceBerryRule;
import org.junit.Rule;
import org.junit.Test;
import org.skye.domain.ArchiveStoreInstance;
import org.skye.domain.Channel;
import org.skye.domain.ChannelArchiveStore;
import org.skye.domain.DomainArchiveStore;
import org.skye.stores.inmemory.InMemoryArchiveStore;
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
        DomainArchiveStore das = new DomainArchiveStore();
        das.setArchiveStoreInstance(asi);
        ChannelArchiveStore cas = new ChannelArchiveStore();
        cas.setDomainArchiveStore(das);
        Channel channel = new Channel();
        channel.getChannelArchiveStores().add(cas);
    }

}
