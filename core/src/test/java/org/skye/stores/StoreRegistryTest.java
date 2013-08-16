package org.skye.stores;

import org.junit.Test;
import org.skye.domain.ArchiveStoreInstance;
import org.skye.domain.DomainArchiveStore;
import org.skye.domain.DomainInformationStore;
import org.skye.stores.inmemory.InMemoryArchiveStore;
import org.skye.stores.inmemory.InMemoryInformationStore;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * A set of basic tests for the registry
 */
public class StoreRegistryTest {

    StoreRegistry registry = new StoreRegistry();

    @Test
    public void checkWeHaveInMemoryStores() {
        assertThat("We have 1 information store in the registry", registry.getInformationStoreCount(), is(equalTo(1)));
        assertThat("We have 1 archive store in the registry", registry.getArchiveStoreCount(), is(equalTo(1)));
    }

    @Test
    public void creationOfInMemoryInformationStore() {
        DomainInformationStore dis = new DomainInformationStore();
        dis.setImplementation(InMemoryInformationStore.IMPLEMENTATION);

        assertThat("We can create an in-memory information store and we get something", registry.build(dis).isPresent());
        assertThat("We can create an in-memory information store and we get an in-memory one", registry.build(dis).get().getUrl().equals("mem://."));

    }

    @Test
    public void creationOfInMemoryArchiveStore() {
        DomainArchiveStore das = new DomainArchiveStore();
        ArchiveStoreInstance asi = new ArchiveStoreInstance();

        asi.setImplementation(InMemoryInformationStore.IMPLEMENTATION);
        das.setArchiveStoreInstance(asi);

        assertThat("We can create an in-memory archive store and we get something", registry.build(das).isPresent());
        assertThat("We can create an in-memory archive store and we get an in-memory one", registry.build(das).get().getClass().equals(InMemoryArchiveStore.class));

    }

}
