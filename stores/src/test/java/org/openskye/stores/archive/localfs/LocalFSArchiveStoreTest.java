package org.openskye.stores.archive.localfs;

import com.google.common.base.Optional;
import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.persist.PersistService;
import org.eobjects.metamodel.UpdateCallback;
import org.eobjects.metamodel.UpdateScript;
import org.eobjects.metamodel.UpdateableDataContext;
import org.eobjects.metamodel.create.ColumnCreationBuilder;
import org.eobjects.metamodel.schema.ColumnType;
import org.eobjects.metamodel.schema.Schema;
import org.eobjects.metamodel.schema.Table;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openskye.core.ArchiveStore;
import org.openskye.core.QueryContext;
import org.openskye.core.QueryableStore;
import org.openskye.core.StructuredObject;
import org.openskye.core.structured.Row;
import org.openskye.domain.*;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.stores.StoreRegistry;
import org.openskye.stores.information.InMemoryTestModule;
import org.openskye.stores.information.jdbc.JDBCStructuredInformationStore;
import org.openskye.task.TaskManager;
import org.openskye.task.step.ArchiveTaskStep;
import org.openskye.task.step.DiscoverTaskStep;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.UUID;

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
    @Inject
    public ObjectMetadataRepository omr;
    @Inject
    public PersistService persistService;

    @Before
    public void setUp() {
        try {
            persistService.start();
        } catch (IllegalStateException e) {
            // Ignore it we are started
        }

        insertTestData(((JDBCStructuredInformationStore) registry.build(getDis("test1")).get()).getDataContext());
        insertTestData(((JDBCStructuredInformationStore) registry.build(getDis("test2")).get()).getDataContext());
    }

    private void insertTestData(UpdateableDataContext dataContext) {
        final Schema schema = dataContext.getDefaultSchema();

        if (schema.getTableCount() == 0) {
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
        }
    }

    public InformationStoreDefinition getDis(String dbName) {
        InformationStoreDefinition dis = new InformationStoreDefinition();
        dis.setId(UUID.randomUUID().toString());
        dis.setImplementation(JDBCStructuredInformationStore.IMPLEMENTATION);
        dis.getProperties().put(JDBCStructuredInformationStore.DRIVER_CLASS, "org.h2.Driver");
        dis.getProperties().put(JDBCStructuredInformationStore.DB_URL, "jdbc:h2:mem:" + dbName);
        dis.getProperties().put(JDBCStructuredInformationStore.USER, "sa");
        dis.getProperties().put(JDBCStructuredInformationStore.PASSWORD, "");
        return dis;
    }

    @Test
    public void ensureWeCanDiscoverObjects() throws Exception {

        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        asi.setId(UUID.randomUUID().toString());
        asi.setImplementation(LocalFSArchiveStore.IMPLEMENTATION);
        Path temp = Files.createTempDirectory("archive-" + UUID.randomUUID().toString());
        asi.getProperties().put(LocalFSArchiveStore.LOCALFS_PATH, temp.toAbsolutePath().toString());
        InformationStoreDefinition dis = getDis("test1");
        dis.setId(UUID.randomUUID().toString());

        ArchiveStoreDefinition das = new ArchiveStoreDefinition();
        das.setId(UUID.randomUUID().toString());

        das.setArchiveStoreInstance(asi);
        ChannelArchiveStore cas = new ChannelArchiveStore();
        cas.setId(UUID.randomUUID().toString());
        cas.setArchiveStoreDefinition(das);
        Channel channel = new Channel();
        channel.setId(UUID.randomUUID().toString());
        channel.getChannelArchiveStores().add(cas);
        channel.setInformationStoreDefinition(dis);
        Project project = new Project();
        project.setId(UUID.randomUUID().toString());
        channel.setProject(project);

        Task discovery = new DiscoverTaskStep(channel).toTask();
        taskManager.submit(discovery);

        Task archive = new ArchiveTaskStep(channel).toTask();
        taskManager.submit(archive);

        long discovered = discovery.getStatistics().getSimpleObjectsFound();
        assertThat("We should have 1 discovered simple objects, not " + discovered, discovered == 1);
        long ingested = archive.getStatistics().getSimpleObjectsProcessed();
        assertThat("We should have 1 ingested simple objects, not " + ingested, ingested == 1);

    }

    @Test
    public void letsArchiveThenQuery() throws Exception {


        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        asi.setImplementation(LocalFSArchiveStore.IMPLEMENTATION);
        Path temp = Files.createTempDirectory("archive-" + UUID.randomUUID().toString());
        asi.getProperties().put(LocalFSArchiveStore.LOCALFS_PATH, temp.toAbsolutePath().toString());
        InformationStoreDefinition dis = getDis("test2");
        ArchiveStoreDefinition das = new ArchiveStoreDefinition();
        das.setId(UUID.randomUUID().toString());
        das.setArchiveStoreInstance(asi);
        ChannelArchiveStore cas = new ChannelArchiveStore();
        cas.setArchiveStoreDefinition(das);
        Channel channel = new Channel();
        channel.getChannelArchiveStores().add(cas);
        channel.setInformationStoreDefinition(dis);
        Project project = new Project();
        project.setId(UUID.randomUUID().toString());
        channel.setProject(project);

        Task discovery = new DiscoverTaskStep(channel).toTask();
        taskManager.submit(discovery);

        Task archive = new ArchiveTaskStep(channel).toTask();
        taskManager.submit(archive);

        long discovered = discovery.getStatistics().getSimpleObjectsFound();
        assertThat("We should have 1 discovered simple objects, not " + discovered, discovered == 1);
        long ingested = archive.getStatistics().getSimpleObjectsProcessed();
        assertThat("We should have 1 ingested simple objects, not " + ingested, ingested == 1);

        Optional<ArchiveStore> archiveStore = registry.build(das);

        assertThat("We got the archive store", archiveStore.isPresent());

        assertThat("It is queryable", archiveStore.get() instanceof QueryableStore);

        QueryableStore queryableStore = (QueryableStore) archiveStore.get();
        QueryContext context = new QueryContext();
        context.getObjects().add(omr.getObjects(dis).iterator().next());
        StructuredObject result = queryableStore.executeQuery(context, "select * from my_table");

        Iterator<Row> iter = result.getRows();
        assertThat("we have a row", iter.hasNext());
        assertThat("result has first row", iter.next() != null);
        assertThat("we have a row", iter.hasNext());
        assertThat("result has second row", iter.next() != null);
    }
}
