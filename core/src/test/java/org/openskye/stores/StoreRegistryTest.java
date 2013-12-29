package org.openskye.stores;

import com.google.guiceberry.junit4.GuiceBerryRule;
import org.junit.Rule;
import org.junit.Test;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.ArchiveStoreInstance;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.guice.InMemoryTestModule;
import org.openskye.stores.inmemory.InMemoryArchiveStore;
import org.openskye.stores.inmemory.InMemoryInformationStore;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * A set of basic tests for the registry
 */
public class StoreRegistryTest {

    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(InMemoryTestModule.class);
    @Inject
    public StoreRegistry registry;

    @Test
    public void checkWeHaveInMemoryStores() {
        assertThat("We have 1 information store in the registry", registry.getInformationStoreCount(), is(equalTo(1)));
        assertThat("We have 1 archive store in the registry", registry.getArchiveStoreCount(), is(equalTo(1)));
    }

    @Test
    public void creationOfInMemoryInformationStore() {
        InformationStoreDefinition dis = new InformationStoreDefinition();
        dis.setImplementation(InMemoryInformationStore.IMPLEMENTATION);

        assertThat("We can create an in-memory information store and we get something", registry.build(dis).isPresent());
        assertThat("We can create an in-memory information store and we get an in-memory one", registry.build(dis).get().getUrl().equals("mem://."));
    }

    @Test
    public void creationOfInMemoryArchiveStore() {
        ArchiveStoreDefinition das = new ArchiveStoreDefinition();
        ArchiveStoreInstance asi = new ArchiveStoreInstance();

        asi.setImplementation(InMemoryInformationStore.IMPLEMENTATION);
        das.setArchiveStoreInstance(asi);

        assertThat("We can create an in-memory archive store and we get something", registry.build(asi).isPresent());
        assertThat("We can create an in-memory archive store and we get an in-memory one", registry.build(asi).get().getClass().equals(InMemoryArchiveStore.class));

    }

}
