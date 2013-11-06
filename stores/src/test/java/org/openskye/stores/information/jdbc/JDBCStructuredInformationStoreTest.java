package org.openskye.stores.information.jdbc;

import com.google.guiceberry.junit4.GuiceBerryRule;
import org.eobjects.metamodel.UpdateCallback;
import org.eobjects.metamodel.UpdateScript;
import org.eobjects.metamodel.UpdateableDataContext;
import org.eobjects.metamodel.schema.ColumnType;
import org.eobjects.metamodel.schema.Schema;
import org.eobjects.metamodel.schema.Table;
import org.junit.Rule;
import org.junit.Test;
import org.openskye.domain.*;
import org.openskye.stores.StoreRegistry;
import org.openskye.stores.information.InMemoryTestModule;
import org.openskye.stores.inmemory.InMemoryArchiveStore;
import org.openskye.task.TaskManager;
import org.openskye.task.step.ArchiveTaskStep;
import org.openskye.task.step.DiscoverTaskStep;

import javax.inject.Inject;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * A test to determine that the {@link org.openskye.stores.information.jdbc.JDBCStructuredInformationStore} is working
 */
public class JDBCStructuredInformationStoreTest {

    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(InMemoryTestModule.class);
    @Inject
    public TaskManager taskManager;
    @Inject
    public StoreRegistry registry;

    public InformationStoreDefinition getLocalMySQLDis() {
        InformationStoreDefinition dis = new InformationStoreDefinition();
        dis.setImplementation(JDBCStructuredInformationStore.IMPLEMENTATION);
        dis.getProperties().put(JDBCStructuredInformationStore.DRIVER_CLASS, "com.mysql.jdbc.Driver");
        dis.getProperties().put(JDBCStructuredInformationStore.DB_URL, "jdbc:mysql://localhost:3306/orion_dev?autoreconnect=true");
        dis.getProperties().put(JDBCStructuredInformationStore.USER, "root");
        dis.getProperties().put(JDBCStructuredInformationStore.PASSWORD, "password");
        return dis;
    }

    public InformationStoreDefinition getInMemoryDis() {
        InformationStoreDefinition dis = new InformationStoreDefinition();
        dis.setImplementation(JDBCStructuredInformationStore.IMPLEMENTATION);
        dis.getProperties().put(JDBCStructuredInformationStore.DRIVER_CLASS, "org.h2.Driver");
        dis.getProperties().put(JDBCStructuredInformationStore.DB_URL, "jdbc:h2:mem:openskye");
        dis.getProperties().put(JDBCStructuredInformationStore.USER, "sa");
        dis.getProperties().put(JDBCStructuredInformationStore.PASSWORD, "");
        return dis;
    }

    @Test
    public void createInformationStore() {
        assertThat("We can create an instance of the JDBC structured information store", registry.build(getInMemoryDis()).isPresent());
    }

    @Test
    public void ensureWeCanDiscoverObjects() {
        assertThat("Get metadata for the store", registry.build(getInMemoryDis()).get().getMetadata() != null);
        JDBCStructuredInformationStore is = (JDBCStructuredInformationStore) registry.build(getInMemoryDis()).get();

        final UpdateableDataContext dataContext = is.getDataContext();
        final Schema schema = dataContext.getDefaultSchema();
        dataContext.executeUpdate(new UpdateScript() {

            public void run(UpdateCallback callback) {

                // CREATING A TABLE

                Table table = callback.createTable(schema, "my_table")
                        .withColumn("name").ofType(ColumnType.VARCHAR)
                        .withColumn("gender").ofType(ColumnType.CHAR)
                        .withColumn("age").ofType(ColumnType.INTEGER)
                        .execute();

                // INSERTING SOME ROWS

                callback.insertInto(table).value("name", "John Doe").value("gender", 'M').value("age", 42).execute();
                callback.insertInto(table).value("name", "Jane Doe").value("gender", 'F').value("age", 42).execute();
            }
        });


        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        asi.setImplementation(InMemoryArchiveStore.IMPLEMENTATION);
        InformationStoreDefinition dis = getInMemoryDis();
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

        Task discover = new DiscoverTaskStep(channel).toTask();
        taskManager.submit(discover);

        assertThat("We have 1 discovered simple objects", discover.getStatistics().getSimpleObjectsDiscovered() == 1);
    }

    // Note this test can't always run on the build server
    public void ensureWeCanArchiveToALocalMySQLDB() {

        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        asi.setImplementation(InMemoryArchiveStore.IMPLEMENTATION);
        InformationStoreDefinition dis = getLocalMySQLDis();
        ArchiveStoreDefinition das = new ArchiveStoreDefinition();
        das.setArchiveStoreInstance(asi);
        ChannelArchiveStore cas = new ChannelArchiveStore();
        cas.setArchiveStoreDefinition(das);
        Channel channel = new Channel();
        channel.getChannelArchiveStores().add(cas);
        channel.setInformationStoreDefinition(dis);

        Task discover = new DiscoverTaskStep(channel).toTask();
        taskManager.submit(discover);

        Task archive = new ArchiveTaskStep(channel).toTask();
        taskManager.submit(archive);

    }

}
