package org.openskye.metadata.impl.jpa;

import com.google.common.base.Optional;
import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.Provider;
import com.google.inject.persist.PersistService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.ObjectSet;
import org.openskye.domain.*;
import org.openskye.domain.dao.*;
import org.openskye.guice.InMemoryTestModule;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.stores.inmemory.InMemoryArchiveStore;
import org.openskye.stores.inmemory.InMemoryInformationStore;
import org.openskye.task.TaskManager;
import org.openskye.task.step.TestTaskStep;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * This class performs unit testing for the jpa package of classes, including
 * {@link ArchiveContentBlock}, {@link ObjectMetadata}, and the
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
    public NodeDAO nodeDAO;
    @Inject
    public ProjectDAO projects;
    @Inject
    public TaskStatisticsDAO taskStatisticsDAO;
    @Inject
    public ArchiveStoreInstanceDAO archiveStoreInstanceDAO;
    @Inject
    public Provider<EntityManager> emf;
    @Inject
    PersistService persistService;

    @Before
    public void checkStarted() {
        try {
            persistService.start();
        } catch (IllegalStateException e) {
            // Ignore it we are started
        }
    }

    @Test
    public void metadataStorageAndRetrieval() {

        emf.get().getTransaction().begin();

        ArchiveStoreInstance asi = new ArchiveStoreInstance();
        asi.setImplementation("test");
        asi.setName("test");
        archiveStoreInstanceDAO.create(asi);
        ArchiveStoreDefinition asd = new ArchiveStoreDefinition();
        asd.setName("Test");
        ArchiveContentBlock acb = new ArchiveContentBlock();
        acb.setChecksum("123");
        acb.setCompressedSize(1000);
        acb.setOriginalSize(1000);

        InformationStoreDefinition isd = new InformationStoreDefinition();
        isd.setName("Test");
        ObjectMetadata objectMetadata = new ObjectMetadata();

        TaskStatistics taskStatistics = new TaskStatistics();
        taskStatisticsDAO.create(taskStatistics);
        Project project = new Project();
        project.setName("Test Project");
        Project newProject = projects.create(project);

        asi.setImplementation(InMemoryArchiveStore.IMPLEMENTATION);
        archiveStoreInstanceDAO.create(asi);
        asd.setArchiveStoreInstance(asi);
        archiveStores.create(asd);
        acb.setArchiveStoreInstance(asi);
        acb.setProject(newProject);
        objectMetadata.getArchiveContentBlocks().add(acb);

        isd.setImplementation(InMemoryInformationStore.IMPLEMENTATION);
        informationStores.create(isd);
        objectMetadata.setInformationStoreId(isd.getId());

        Node node = new Node();
        nodeDAO.create(node);

        TestTaskStep step = new TestTaskStep(newProject, node, 2, 1, true);
        Task task = step.toTask();
        task.setStatistics(taskStatistics);
        tasks.create(task);
        objectMetadata.setTaskId(task.getId());

        //
        // Start tests
        //

        ObjectMetadata result = omr.put(objectMetadata);
        Optional<ObjectMetadata> metadataOutput = omr.get(result.getId());
        String outputIsdId = metadataOutput.get().getInformationStoreId();

        // Test that the Persisted metadataOutput is present, and that it
        // matches the input.
        assertThat("object should be found", metadataOutput.isPresent());
        assertThat("object should have the correct information store definition",
                outputIsdId,
                is(equalTo(isd.getId())));

        Optional<InformationStoreDefinition> outputIsd = informationStores.get(outputIsdId);

        assertThat("information store associated with object is found", outputIsd.isPresent());

        Optional<ArchiveContentBlock> acbOutput = metadataOutput.get().getArchiveContentBlock(asi);

        // Test that the persisted ACB is present, and that it
        // matches the input.
        assertThat("object should have the correct archive content block",
                acbOutput.isPresent());
        assertThat("object should have the correct archive store instance",
                acbOutput.get().getArchiveStoreInstance().getId(),
                is(equalTo(asi.getId())));

        // Test that the persisted metadata can be retrieved by information
        // store definition.
        Iterable<ObjectMetadata> metadataList = omr.getObjects(isd);

        boolean isFound = false;
        for (ObjectMetadata metadata : metadataList) {
            if (metadata.getId().equals(objectMetadata.getId()))
                isFound = true;
        }

        assertThat("get objects by information store definition should contain created metadata",
                isFound);

        // Test that the persisted metadata can be retrieved by its associated
        // task.
        isFound = false;
        metadataList = omr.getObjects(task);

        for (ObjectMetadata metadata : metadataList) {
            if (metadata.getId().equals(objectMetadata.getId()))
                isFound = true;
        }

        assertThat("get objects by task should contain created metadata",
                isFound);

        ObjectSet objectSet = omr.createObjectSet("TestObjectSet");
        Optional<ObjectSet> setOutput = omr.getObjectSet(objectSet.getId());

        assertThat("object set can be retrieved by id",
                setOutput.isPresent());

        // Ensure that we can retrieve all object sets.
        isFound = false;
        Iterable<ObjectSet> setList = omr.getAllObjectSets();

        for (ObjectSet set : setList) {
            if (set.getId().equals(objectSet.getId()))
                isFound = true;
        }

        assertThat("object set is found in list of all sets",
                isFound);

        omr.addObjectToSet(objectSet, metadataOutput.get());

        assertThat("object was added to object set",
                omr.isObjectInSet(objectSet, metadataOutput.get()));

        isFound = false;
        metadataList = omr.getObjects(objectSet);

        for (ObjectMetadata metadata : metadataList) {
            if (metadata.getId().equals(objectMetadata.getId()))
                isFound = true;
        }

        assertThat("object is found in object set",
                isFound);

        System.out.println("About to DELETE");

        omr.removeObjectFromSet(objectSet, metadataOutput.get());

        System.out.println("DELETED");

        assertThat("object has been removed from object set",
                (!omr.isObjectInSet(objectSet, metadataOutput.get())));

        omr.deleteObjectSet(objectSet);

        setOutput = omr.getObjectSet(objectSet.getId());

        assertThat("object set not has been removed",
                (!setOutput.isPresent()));

    }
}
