package org.skye.stores.information;

import com.google.guiceberry.junit4.GuiceBerryRule;
import org.eobjects.metamodel.UpdateCallback;
import org.eobjects.metamodel.UpdateScript;
import org.eobjects.metamodel.UpdateableDataContext;
import org.eobjects.metamodel.schema.ColumnType;
import org.eobjects.metamodel.schema.Schema;
import org.eobjects.metamodel.schema.Table;
import org.junit.Rule;
import org.junit.Test;
import org.skye.domain.*;
import org.skye.stores.StoreRegistry;
import org.skye.stores.inmemory.InMemoryArchiveStore;
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

    public DomainInformationStore getDis() {
        DomainInformationStore dis = new DomainInformationStore();
        dis.setImplementation(JDBCStructuredInformationStore.IMPLEMENTATION);
        dis.getProperties().put(JDBCStructuredInformationStore.DRIVER_CLASS, "org.h2.Driver");
        dis.getProperties().put(JDBCStructuredInformationStore.DB_URL, "jdbc:h2:mem:skye");
        dis.getProperties().put(JDBCStructuredInformationStore.USER, "sa");
        dis.getProperties().put(JDBCStructuredInformationStore.PASSWORD, "");
        return dis;
    }

    @Test
    public void createInformationStore() {
        assertThat("We can create an instance of the JDBC structured information store", registry.build(getDis()).isPresent());
    }

    @Test
    public void ensureWeCanSeeASimpleObject() {
        assertThat("Get metadata for the store", registry.build(getDis()).get().getMetadata() != null);
        JDBCStructuredInformationStore is = (JDBCStructuredInformationStore) registry.build(getDis()).get();

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
        DomainInformationStore dis = getDis();
        DomainArchiveStore das = new DomainArchiveStore();
        das.setArchiveStoreInstance(asi);
        ChannelArchiveStore cas = new ChannelArchiveStore();
        cas.setDomainArchiveStore(das);
        Channel channel = new Channel();
        channel.getChannelArchiveStores().add(cas);
        channel.setDomainInformationStore(dis);

        Task newTask = new Task();
        newTask.setChannel(channel);
        newTask.setTaskType(TaskType.DISCOVER);
        taskManager.submit(newTask);

    }

}
