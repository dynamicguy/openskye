package org.openskye.metadata.impl.jpa;

import com.google.common.base.Optional;
import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.Provider;
import org.junit.Rule;
import org.junit.Test;
import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.ObjectSet;
import org.openskye.domain.*;
import org.openskye.domain.dao.*;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.stores.inmemory.InMemoryArchiveStore;
import org.openskye.stores.inmemory.InMemoryInformationStore;
import org.openskye.task.TaskManager;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * This class performs unit testing for the jpa package of classes, including
 * {@link JPAArchiveContentBlock}, {@link JPAObjectMetadata}, and the
 * {@link JPAObjectMetadataRepository}.
 */
public class JPAObjectMetadataRepositoryTest {
    @Rule
    public final GuiceBerryRule guiceBerryRule = new GuiceBerryRule(InMemoryTestModule.class);
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
    public ArchiveStoreInstanceDAO archiveStoreInstanceDAO;
    @Inject
    public Provider<EntityManager> emf;

    @Test
    public void metadataStorageAndRetrieval() {
        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        asi.setImplementation("test");
        asi.setName("test");
        archiveStoreInstanceDAO.create(asi);
        ArchiveStoreDefinition asd = new ArchiveStoreDefinition();
        asd.setName("Test");
        ArchiveContentBlock acb = new JPAArchiveContentBlock().toArchiveContentBlock();
        InformationStoreDefinition isd = new InformationStoreDefinition();
        isd.setName("Test");
        ObjectMetadata objectMetadata = new ObjectMetadata();
        Optional<ObjectMetadata> metadataOutput = Optional.absent();
        Optional<ArchiveContentBlock> acbOutput = Optional.absent();
        Iterable<ObjectMetadata> metadataList = null;
        boolean isFound = false;
        Task task = new Task();
        TaskStatistics taskStatistics = new TaskStatistics();
        ObjectSet objectSet = null;
        Optional<ObjectSet> setOutput = Optional.absent();
        Iterable<ObjectSet> setList;

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


        objectSet = this.omr.createObjectSet("TestObjectSet");
        setOutput = this.omr.getObjectSet(objectSet.getId());

        assertThat("object set can be retrieved by id",
                setOutput.isPresent());

        // Ensure that we can retreive all object sets.
        isFound = false;
        setList = this.omr.getAllObjectSets();

        for (ObjectSet set : setList) {
            if (set.getId().equals(objectSet.getId()))
                isFound = true;
        }

        assertThat("object set is found in list of all sets",
                isFound);

        this.omr.addObjectToSet(objectSet, metadataOutput.get());

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

        this.omr.removeObjectToSet(objectSet, metadataOutput.get());

        assertThat("object has been removed from object set",
                (!this.omr.isObjectInSet(objectSet, metadataOutput.get())));

        this.omr.deleteObjectSet(objectSet);

        setOutput = this.omr.getObjectSet(objectSet.getId());

        assertThat("object set has been removed",
                (!setOutput.isPresent()));

        return;
    }
}
