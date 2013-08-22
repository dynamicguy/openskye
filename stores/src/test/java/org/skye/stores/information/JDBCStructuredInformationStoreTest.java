package org.skye.stores.information;

import com.google.guiceberry.junit4.GuiceBerryRule;
import org.junit.Rule;
import org.junit.Test;
import org.skye.domain.DomainInformationStore;
import org.skye.stores.StoreRegistry;
import org.skye.task.TaskManager;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * A test to determine that the {@link JDBCStructuredInformationStore} is working
 */
public class JDBCStructuredInformationStoreTest {

    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(InMemoryTestModule.class);
    @Inject
    public TaskManager taskManager;
    @Inject
    public StoreRegistry registry;

    @Test
    public void createInformationStore() {
        DomainInformationStore dis = new DomainInformationStore();
        dis.setImplementation(JDBCStructuredInformationStore.IMPLEMENTATION);

        assertThat("We can create an instance of the JDBC structured information store", registry.build(dis).isPresent());
    }

    @Test
    public void ensureWeCanSeeASimpleObject() {
        DomainInformationStore dis = new DomainInformationStore();
        dis.setImplementation(JDBCStructuredInformationStore.IMPLEMENTATION);

        assertThat("We can create an instance of the JDBC structured information store", registry.build(dis).isPresent());

        JDBCStructuredInformationStore is = (JDBCStructuredInformationStore) registry.build(dis).get();



    }

}
