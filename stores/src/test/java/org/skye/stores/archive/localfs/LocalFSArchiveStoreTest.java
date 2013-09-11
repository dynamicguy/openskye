package org.skye.stores.archive.localfs;

import com.google.guiceberry.junit4.GuiceBerryRule;
import org.eobjects.metamodel.UpdateCallback;
import org.eobjects.metamodel.UpdateScript;
import org.eobjects.metamodel.UpdateableDataContext;
import org.eobjects.metamodel.create.ColumnCreationBuilder;
import org.eobjects.metamodel.schema.ColumnType;
import org.eobjects.metamodel.schema.Schema;
import org.eobjects.metamodel.schema.Table;
import org.junit.Rule;
import org.junit.Test;
import org.skye.domain.*;
import org.skye.stores.StoreRegistry;
import org.skye.stores.information.InMemoryTestModule;
import org.skye.stores.information.jdbc.JDBCStructuredInformationStore;
import org.skye.task.TaskManager;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test basic functionality of the {@link LocalFSArchiveStore}
 */
public class LocalFSArchiveStoreTest {

    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(InMemoryTestModule.class);
    @Inject
    public TaskManager taskManager;
    @Inject
    public StoreRegistry registry;

    public InformationStoreDefinition getDis() {
        InformationStoreDefinition dis = new InformationStoreDefinition();
        dis.setImplementation(JDBCStructuredInformationStore.IMPLEMENTATION);
        dis.getProperties().put(JDBCStructuredInformationStore.DRIVER_CLASS, "org.h2.Driver");
        dis.getProperties().put(JDBCStructuredInformationStore.DB_URL, "jdbc:h2:mem:skye");
        dis.getProperties().put(JDBCStructuredInformationStore.USER, "sa");
        dis.getProperties().put(JDBCStructuredInformationStore.PASSWORD, "");
        return dis;
    }

    @Test
    public void ensureWeCanDiscoverObjects() {
        assertThat("Get metadata for the store", registry.build(getDis()).get().getMetadata() != null);
        JDBCStructuredInformationStore is = (JDBCStructuredInformationStore) registry.build(getDis()).get();

        final UpdateableDataContext dataContext = is.getDataContext();
        final Schema schema = dataContext.getDefaultSchema();
        dataContext.executeUpdate(new UpdateScript() {

            public void run(UpdateCallback callback) {

                // CREATING A TABLE

                ColumnCreationBuilder tableCreator = callback.createTable(schema, "my_table")
                        .withColumn("name").ofType(ColumnType.VARCHAR)
                        .withColumn("gender").ofType(ColumnType.CHAR)
                        .withColumn("age").ofType(ColumnType.INTEGER);


                Table table = tableCreator.execute();

                // INSERTING SOME ROWS

                callback.insertInto(table).value("name", "John Doe").value("gender", 'M').value("age", 42).execute();
                callback.insertInto(table).value("name", "Jane Doe").value("gender", 'F').value("age", 42).execute();
            }
        });


        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        asi.setImplementation(LocalFSArchiveStore.IMPLEMENTATION);
        asi.getProperties().put(LocalFSArchiveStore.LOCALFS_PATH, "/tmp/pj");
        InformationStoreDefinition dis = getDis();
        ArchiveStoreDefinition das = new ArchiveStoreDefinition();
        das.setArchiveStoreInstance(asi);
        ChannelArchiveStore cas = new ChannelArchiveStore();
        cas.setArchiveStoreDefinition(das);
        Channel channel = new Channel();
        channel.getChannelArchiveStores().add(cas);
        channel.setInformationStoreDefinition(dis);

        Task discovery = new Task();
        discovery.setChannel(channel);
        discovery.setTaskType(TaskType.DISCOVER);
        taskManager.submit(discovery);

        Task archive = new Task();
        archive.setChannel(channel);
        archive.setTaskType(TaskType.ARCHIVE);
        taskManager.submit(archive);

        assertThat("We have 1 discovered simple objects", discovery.getStatistics().getSimpleObjectsDiscovered() == 1);
        assertThat("We have 1 ingested simple objects", archive.getStatistics().getSimpleObjectsIngested() == 1);

    }
}
