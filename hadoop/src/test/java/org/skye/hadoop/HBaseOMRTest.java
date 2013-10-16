package org.skye.hadoop;

import com.google.common.base.Optional;
import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.Provider;
import com.google.common.*;
import org.junit.Rule;
import org.junit.Test;
import org.skye.core.ArchiveContentBlock;
import org.skye.core.ObjectMetadata;
import org.skye.core.ObjectSet;
import org.skye.domain.*;
import org.skye.domain.dao.*;
import org.skye.metadata.ObjectMetadataRepository;
import org.skye.metadata.impl.jpa.JPAArchiveContentBlock;
import org.skye.stores.inmemory.InMemoryArchiveStore;
import org.skye.stores.inmemory.InMemoryInformationStore;
import org.skye.task.TaskManager;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created with IntelliJ IDEA.
 * User: atcmostafavi
 * Date: 10/14/13
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class HBaseOMRTest {
    @Rule
    public final GuiceBerryRule guiceBerryRule = new GuiceBerryRule(HadoopTestModule.class);
    @Inject
    public TaskManager taskManager;
    @Inject
    public ObjectMetadataRepository omr;
    @Inject
    public InformationStoreDefinitionDAO informationStores;
    @Inject
    public ArchiveStoreDefinitionDAO archiveStores;
    @Inject
    public TaskDAO tasks;
    @Inject
    public TaskStatisticsDAO taskStatisticsDAO;
    @Inject
    public ArchiveStoreInstanceDAO archiveStoreInstances;
    @Inject
    public Provider<EntityManager> emf;

    @Test
    public void metadataStorageAndRetrieval() {
        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        ArchiveStoreDefinition asd = new ArchiveStoreDefinition();
        ArchiveContentBlock acb = new JPAArchiveContentBlock().toArchiveContentBlock();
        InformationStoreDefinition isd = new InformationStoreDefinition();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        Optional<ObjectMetadata> metadataOutput = Optional.absent();
        Optional<ArchiveContentBlock> acbOutput = Optional.absent();
        Iterable<ObjectMetadata> metadataList = null;
        boolean isFound = false;
        Task task = new Task();
        TaskStatistics taskStatistics = new TaskStatistics();
        ObjectSet objectSet = null;
        Optional<ObjectSet> setOutput = Optional.absent();

        emf.get().getTransaction().begin();

        asi.setImplementation(InMemoryArchiveStore.IMPLEMENTATION);
        this.archiveStoreInstances.create(asi);
        asd.setArchiveStoreInstance(asi);
        this.archiveStores.create(asd);
        acb.setArchiveStoreDefinitionId(asd.getId());
        objectMetadata.getArchiveContentBlocks().add(acb);

        isd.setImplementation(InMemoryInformationStore.IMPLEMENTATION);
        this.informationStores.create(isd);
        objectMetadata.setInformationStoreId(isd.getId());

        this.taskStatisticsDAO.create(taskStatistics);
        task.setTargetInformationStoreDefinition(isd);
        task.setStatistics(taskStatistics);
        this.tasks.create(task);
        objectMetadata.setTaskId(task.getId());

        this.omr.put(objectMetadata);

        emf.get().getTransaction().commit();

        metadataOutput = this.omr.get(objectMetadata.getId());

        // Test that the Persisted metadataOutput is present, and that it
        // matches the input.
        assertThat("object should be found", metadataOutput.isPresent());
        assertThat("object should have the correct information store definition",
                metadataOutput.get().getInformationStoreId(),
                is(equalTo(isd.getId())));

        acbOutput = metadataOutput.get().getArchiveContentBlock(asd.getId());

        // Test that the persisted ACB is present, and that it
        // matches the input.
        assertThat("object should have the correct archive content block",
                acbOutput.isPresent());
        assertThat("object should have the correct archive store definition",
                acbOutput.get().getArchiveStoreDefinitionId(),
                is(equalTo(asd.getId())));

        // Test that the persisted metadata can be retrieved by information
        // store definition.
        metadataList = this.omr.getObjects(isd);

        for (ObjectMetadata metadata : metadataList) {
            if (metadata.getId().equals(objectMetadata.getId()))
                isFound = true;
        }

        assertThat("get objects by information store definition should contain created metadata",
                isFound);

        // Test that the persisted metadata can be retrieved by its associated
        // task.
        isFound = false;
        metadataList = this.omr.getObjects(task);

        for (ObjectMetadata metadata : metadataList) {
            if (metadata.getId().equals(objectMetadata.getId()))
                isFound = true;
        }

        assertThat("get objects by task should contain created metadata",
                isFound);


        // Test that an ObjectSet can be created.
        this.emf.get().getTransaction().begin();

        objectSet = this.omr.createObjectSet("TestObjectSet");
        setOutput = this.omr.getObjectSet(objectSet.getId());

        this.emf.get().getTransaction().commit();

        assertThat("object set can be retrieved by id",
                setOutput.isPresent());

        // Test that an object can be added to the set.
        this.emf.get().getTransaction().begin();

        this.omr.addObjectToSet(objectSet, metadataOutput.get());

        this.emf.get().getTransaction().commit();

        assertThat("object can be added to object set",
                this.omr.isObjectInSet(objectSet, metadataOutput.get()));

        isFound = false;
        metadataList = this.omr.getObjects(objectSet);

        for (ObjectMetadata metadata : metadataList) {
            if (metadata.getId().equals(objectMetadata.getId()))
                isFound = true;
        }

        assertThat("object is found in object set",
                isFound);

        // Test that an object can be removed from the object set.
        this.emf.get().getTransaction().begin();

        this.omr.removeObjectToSet(objectSet, metadataOutput.get());

        this.emf.get().getTransaction().commit();

        assertThat("object has been removed from object set",
                (!this.omr.isObjectInSet(objectSet, metadataOutput.get())));

        // Test that an object set can be removed.
        this.emf.get().getTransaction().begin();

        this.omr.deleteObjectSet(objectSet);

        this.emf.get().getTransaction().commit();

        setOutput = this.omr.getObjectSet(objectSet.getId());

        assertThat("object set has been removed",
                (!setOutput.isPresent()));

        return;
    }
}
