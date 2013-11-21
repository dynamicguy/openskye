package org.openskye.resource;

import com.google.common.base.Optional;
import com.google.guiceberry.junit4.GuiceBerryRule;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Rule;
import org.openskye.core.*;
import org.openskye.domain.*;
import org.openskye.domain.dao.*;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;
import org.openskye.stores.StoreRegistry;
import org.openskye.stores.inmemory.InMemoryInformationStore;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: joshua
 * Date: 11/11/13
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractObjectTest
{
    @Rule
    public final GuiceBerryRule guiceBerryRule = new GuiceBerryRule(ObjectMetadataModule.class);

    public static ObjectMetadataRepository repository = mock(ObjectMetadataRepository.class);

    public static ObjectMetadataSearch search = mock(ObjectMetadataSearch.class);

    public static InformationStoreDefinitionDAO informationStores = mock(InformationStoreDefinitionDAO.class);

    public static TaskDAO tasks = mock(TaskDAO.class);

    public static ProjectDAO projects = mock(ProjectDAO.class);

    public static DomainDAO domains = mock(DomainDAO.class);

    public static StoreRegistry registry = mock(StoreRegistry.class);

    public static ArchiveStore archiveStore = mock(ArchiveStore.class);

    @Inject
    protected SkyeSession session;

    protected Subject subject = mock(Subject.class);

    protected ObjectMetadata metadataInstance = new ObjectMetadata();
    protected ObjectSet objectSetInstance = new ObjectSet();
    protected String setName = "Test Set";

    protected List<ObjectMetadata> metadataList = new ArrayList<>();
    protected List<InformationStoreDefinition> isdList = new ArrayList<>();
    protected List<Task> taskList = new ArrayList<>();
    protected List<Project> projectList = new ArrayList<>();
    protected List<String> pathList = new ArrayList<>();
    protected List<ObjectMetadata> metadataByIsdList = new ArrayList<>();
    protected List<ObjectMetadata> metadataByTaskList = new ArrayList<>();
    protected List<ObjectMetadata> metadataSearchList = new ArrayList<>();
    protected List<ArchiveContentBlock> acbList = new ArrayList<>();
    protected List<ObjectSet> objectSetList = new ArrayList<>();
    protected InformationStoreDefinition isdSearch;
    protected Task taskSearch;
    protected ArchiveStoreDefinition asd = new ArchiveStoreDefinition();
    protected InputStream inputStream;

    protected final String pathA = "/tmp/sourceA/";
    protected final String pathB = "/tmp/sourceB/";

    protected String pathSearch;

    protected Project projectSearch;

    protected PaginatedResult<ObjectMetadata> metadataResult = new PaginatedResult<>();
    protected PaginatedResult<ObjectMetadata> metadataByIsdResult = new PaginatedResult<>();
    protected PaginatedResult<ObjectMetadata> metadataByTaskResult = new PaginatedResult<>();
    protected PaginatedResult<ObjectMetadata> metadataSearchResult = new PaginatedResult<>();
    protected PaginatedResult<ArchiveContentBlock> acbResult = new PaginatedResult<>();
    protected PaginatedResult<ObjectSet> objectSetResult = new PaginatedResult<>();

    protected Response expectedResponse = Response.ok().build();

    @Before
    public void setUp() throws Exception
    {
        Project project;
        InformationStoreDefinition isd;
        List<InformationStoreDefinition> projectIsdList;
        Task task;
        String path;
        ArchiveContentBlock acb;
        int numberOfObjects = 24;

        // Create a couple of paths, which represent folders that the objects will use.
        pathList.add(pathA);
        pathList.add(pathB);

        // Create a couple of projects and information store definitions.
        project = new Project();
        project.setId(UUID.randomUUID().toString());
        project.setDomain(session.getDomain());

        isd = new InformationStoreDefinition();
        isd.setId(UUID.randomUUID().toString());
        isd.setImplementation(InMemoryInformationStore.IMPLEMENTATION);
        isd.setName("ISD 1");
        isd.setProject(project);

        projectIsdList = new ArrayList<>();
        projectIsdList.add(isd);
        project.setInformationStores(projectIsdList);

        when(projects.get(project.getId())).thenReturn(Optional.of(project));
        when(informationStores.get(isd.getId())).thenReturn(Optional.of(isd));

        projectList.add(project);
        isdList.add(isd);

        project = new Project();
        project.setId(UUID.randomUUID().toString());
        project.setDomain(session.getDomain());

        isd = new InformationStoreDefinition();
        isd.setId(UUID.randomUUID().toString());
        isd.setImplementation(InMemoryInformationStore.IMPLEMENTATION);
        isd.setName("ISD 2");
        isd.setProject(project);

        projectIsdList = new ArrayList<>();
        projectIsdList.add(isd);
        project.setInformationStores(projectIsdList);

        when(projects.get(project.getId())).thenReturn(Optional.of(project));
        when(informationStores.get(isd.getId())).thenReturn(Optional.of(isd));

        projectList.add(project);
        isdList.add(isd);

        // Create four tasks...
        task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setProject(projectList.get(0));

        when(tasks.get(task.getId())).thenReturn(Optional.of(task));
        taskList.add(task);

        task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setProject(projectList.get(1));

        when(tasks.get(task.getId())).thenReturn(Optional.of(task));
        taskList.add(task);

        task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setProject(projectList.get(0));

        when(tasks.get(task.getId())).thenReturn(Optional.of(task));
        taskList.add(task);

        task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setProject(projectList.get(1));

        when(tasks.get(task.getId())).thenReturn(Optional.of(task));
        taskList.add(task);

        // Now we create some objects.
        for(int i = 0; i < numberOfObjects; i++)
        {
            ObjectMetadata metadata = new ObjectMetadata();
            int isdIndex = i % 2;
            int taskIndex = i % 4;

            project = projectList.get(isdIndex);
            isd = isdList.get(isdIndex);
            task = taskList.get(taskIndex);
            path = pathList.get(isdIndex) + i;

            metadata.setInformationStoreId(isd.getId());
            metadata.setPath(path);
            metadata.setProject(project);
            metadata.setTaskId(task.getId());

            when(repository.get(metadata.getId())).thenReturn(Optional.of(metadata));
            metadataList.add(metadata);

            if(isdIndex == 0)
            {
                metadataByIsdList.add(metadata);
                metadataSearchList.add(metadata);
            }

            if(taskIndex == 0)
                metadataByTaskList.add(metadata);
        }

        isdSearch = isdList.get(0);
        taskSearch = taskList.get(0);
        projectSearch = projectList.get(0);

        when(repository.getObjects(isdSearch)).thenReturn(metadataByIsdList);
        when(repository.getObjects(taskSearch)).thenReturn(metadataByTaskList);

        metadataByIsdResult = new PaginatedResult<>(metadataByIsdList);
        metadataByTaskResult = new PaginatedResult<>(metadataByTaskList);
        metadataSearchResult = new PaginatedResult<>(metadataSearchList);

        User user = session.getUser();
        Domain domain = session.getDomain();

        when(subject.getPrincipal()).thenReturn(user);

        when(domains.get(session.getDomain().getId())).thenReturn(Optional.of(domain));

        asd = new ArchiveStoreDefinition();
        asd.setId(UUID.randomUUID().toString());

        acb = new ArchiveContentBlock();
        acb.setId(UUID.randomUUID().toString());
        acb.setArchiveStoreDefinitionId(asd.getId());
        acbList.add(acb);
        acbResult = new PaginatedResult<>(acbList);
        metadataInstance.setArchiveContentBlocks(acbList);
        metadataInstance.setPath(pathA);

        when(repository.put(any(ObjectMetadata.class))).thenReturn(metadataInstance);

        when(repository.get(metadataInstance.getId())).thenReturn(Optional.of(metadataInstance));
        metadataList.add(metadataInstance);

        when(repository.getArchiveStoreDefinition(any(ArchiveContentBlock.class))).thenReturn(asd);
        when(registry.build(any(ArchiveStoreDefinition.class))).thenReturn(Optional.of(archiveStore));

        String hello = "Hello World";
        inputStream = new ByteArrayInputStream(hello.getBytes("UTF-8"));

        when(archiveStore.getStream(any(ObjectMetadata.class))).thenReturn(Optional.of(inputStream));

        when(repository.getAllObjects()).thenReturn(metadataList);
        metadataResult = new PaginatedResult<>(metadataList);

        pathSearch = URLEncoder.encode(pathA, "UTF-8");

        when(search.search(eq(pathA))).thenReturn(metadataSearchList);
        when(search.search(any(Project.class), eq(pathA))).thenReturn(metadataSearchList);

        objectSetInstance.setId(UUID.randomUUID().toString());
        objectSetInstance.setName(setName);
        when(repository.createObjectSet(setName)).thenReturn(objectSetInstance);

        when(repository.getObjectSet(objectSetInstance.getId())).thenReturn(Optional.of(objectSetInstance));

        objectSetList.add(objectSetInstance);
        objectSetResult = new PaginatedResult<>(objectSetList);

        when(repository.getAllObjectSets()).thenReturn(objectSetList);

        when(repository.isObjectInSet(objectSetInstance, metadataInstance)).thenReturn(true);

        when(repository.getObjects(objectSetInstance)).thenReturn(metadataList);
    }

}
