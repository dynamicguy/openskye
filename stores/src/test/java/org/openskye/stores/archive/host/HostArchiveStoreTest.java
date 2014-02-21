package org.openskye.stores.archive.host;

import com.google.common.base.Optional;
import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import org.eobjects.metamodel.UpdateCallback;
import org.eobjects.metamodel.UpdateScript;
import org.eobjects.metamodel.UpdateableDataContext;
import org.eobjects.metamodel.create.ColumnCreationBuilder;
import org.eobjects.metamodel.schema.ColumnType;
import org.eobjects.metamodel.schema.Schema;
import org.eobjects.metamodel.schema.Table;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openskye.core.*;
import org.openskye.core.structured.Row;
import org.openskye.domain.*;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.node.NodeManager;
import org.openskye.stores.StoreRegistry;
import org.openskye.stores.information.InMemoryTestModule;
import org.openskye.stores.information.jdbc.JDBCStructuredInformationStore;
import org.openskye.stores.inmemory.InMemoryUnstructuredObject;
import org.openskye.task.TaskManager;
import org.openskye.task.step.ArchiveTaskStep;
import org.openskye.task.step.DiscoverTaskStep;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Test basic functionality of the {@link HostArchiveStore}
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HostArchiveStoreTest {

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
    @Inject
    private Injector injector;

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
        asi.setImplementation(HostArchiveStore.IMPLEMENTATION);
        Path temp = Files.createTempDirectory("archive-" + UUID.randomUUID().toString());
        asi.getProperties().put(HostArchiveStore.FILE_PATH, temp.toAbsolutePath().toString());
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
        Node node = new Node();
        node.setId(UUID.randomUUID().toString());
        NodeManager.setNode(node);

        Task discovery = new DiscoverTaskStep(channel, node).toTask();
        discovery.setId(UUID.randomUUID().toString());
        taskManager.submit(discovery);

        Task archive = new ArchiveTaskStep(channel, node).toTask();
        archive.setId(UUID.randomUUID().toString());
        taskManager.submit(archive);

        long discovered = discovery.getStatistics().getSimpleObjectsFound();
        assertThat("We should have 1 discovered simple objects, not " + discovered, discovered == 1);
        long ingested = archive.getStatistics().getSimpleObjectsProcessed();
        assertThat("We should have 1 ingested simple objects, not " + ingested, ingested == 1);
        temp.toFile().deleteOnExit();
    }

    @Test
    public void letsArchiveThenQuery() throws Exception {


        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        asi.setImplementation(HostArchiveStore.IMPLEMENTATION);
        asi.setId(UUID.randomUUID().toString());
        Path temp = Files.createTempDirectory("archive-" + UUID.randomUUID().toString());
        asi.getProperties().put(HostArchiveStore.FILE_PATH, temp.toAbsolutePath().toString());
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
        Node node = new Node();
        node.setId(UUID.randomUUID().toString());
        NodeManager.setNode(node);

        Task discovery = new DiscoverTaskStep(channel, node).toTask();
        discovery.setId(UUID.randomUUID().toString());
        taskManager.submit(discovery);
        long discovered = discovery.getStatistics().getSimpleObjectsFound();
        assertThat("We should have 1 discovered simple objects, not " + discovered, discovered == 1);

        Task archive = new ArchiveTaskStep(channel, node).toTask();
        archive.setId(UUID.randomUUID().toString());
        taskManager.submit(archive);
        long ingested = archive.getStatistics().getSimpleObjectsProcessed();
        assertThat("We should have 1 ingested simple objects, not " + ingested, ingested == 1);

        Optional<ArchiveStore> archiveStore = registry.build(asi);

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
        temp.toFile().deleteOnExit();
    }

    @Test
    public void UnstructuredObjectTest() throws Exception {
        String s = File.separator;

        // Construct a mock node
        Node node = new Node();
        node.setHostname("localhost");
        node.setId(UUID.randomUUID().toString());
        NodeManager.setNode(node);

        // Construct a mock archive store
        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        asi.setImplementation(HostArchiveStore.IMPLEMENTATION);
        String testId = UUID.randomUUID().toString();
        Path archivePath = Files.createTempDirectory("archive-" + testId);
        Path tmpPath = Files.createTempDirectory("tmp-" + testId);
        asi.getProperties().put(HostArchiveStore.FILE_PATH, archivePath.toAbsolutePath().toString());
        asi.getProperties().put(HostArchiveStore.TMP_PATH, tmpPath.toAbsolutePath().toString());
        ArchiveStoreDefinition das = new ArchiveStoreDefinition();
        das.setId(UUID.randomUUID().toString());
        das.setArchiveStoreInstance(asi);
        HostArchiveStore store = new HostArchiveStore();
        store.initialize(asi);
        injector.injectMembers(store);

        // Build an unstructured simple object residing in memory
        Project project = new Project();
        project.setName("UnstructuredObjectTest Project");
        project.setId(UUID.randomUUID().toString());
        String objectId = UUID.randomUUID().toString();
        String path = "UnstructuredObjectTest" + s + "123.txt";
        String contentIn = "This is a test of unstructured object archiving";
        InMemoryUnstructuredObject objectIn = new InMemoryUnstructuredObject(project,objectId,path,contentIn);

        // Put the object into the store
        ArchiveStoreWriter writer = store.getWriter(null);
        SimpleObject objectPut = writer.put(objectIn);
        writer.close();

        // Get the object back out of the store and read it
        ObjectMetadata omPut = objectPut.getObjectMetadata();
        HostArchiveUnstructuredObject objectOut = (HostArchiveUnstructuredObject) store.materialize(omPut).get();
        BufferedReader reader = new BufferedReader(new InputStreamReader(objectOut.getInputStream()) );
        String contentOut = reader.readLine();

        assertThat("Content retrieved matches content stored", contentOut, is(equalTo(contentIn)));
        archivePath.toFile().deleteOnExit();
    }
}
