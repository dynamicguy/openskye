package org.openskye.resource;

import com.google.inject.Injector;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.apache.shiro.util.ThreadContext;
import org.junit.ClassRule;
import org.junit.Test;
import org.openskye.domain.Channel;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.Project;
import org.openskye.domain.Task;
import org.openskye.domain.dao.*;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;
import org.openskye.task.TaskManager;
import org.openskye.task.step.*;

import javax.ws.rs.core.MediaType;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TaskResourceTest extends ProjectSpecificResourceTest<Task> {

    public static final TaskDAO dao = mock(TaskDAO.class);
    public static final ChannelDAO channelDAO = mock(ChannelDAO.class);
    public static final TaskLogDAO taskLogDAO = mock(TaskLogDAO.class);
    public static final TaskManager taskManager = mock(TaskManager.class);
    public static final Injector injector = mock(Injector.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TaskResource(dao, channelDAO, taskManager, taskLogDAO, injector))
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
            .build();
    private final Task task = new Task();
    private PaginatedResult<Task> expectedResult = new PaginatedResult<>();

    @Override
    public void setUp(){
        super.setUp();
        Project project = new Project();
        project.setId(projectID);
        task.setProject(project);
    }

    @Override
    public String getSingular() {
        return "task";
    }

    @Override
    public String getPlural() {
        return "tasks";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public Task getInstance() {
        return task;
    }

    @Override
    public AbstractPaginatingDAO getDAO() {
        return dao;
    }

    @Override
    public PaginatedResult getExpectedResult() {
        return expectedResult;
    }

    private Project mockProject() {
        Project project = new Project();
        project.setName("Mock Project");
        project.setId(projectID);
        return project;
    }

    private Channel mockChannel() {
        Channel channel = new Channel();
        channel.setName("Mock Channel");
        channel.setProject(mockProject());
        channel.setInformationStoreDefinition(mockStore());
        return channel;
    }

    private InformationStoreDefinition mockStore() {
        InformationStoreDefinition store = new InformationStoreDefinition();
        store.setName("Mock Store");
        store.setProject(mockProject());
        store.setImplementation("LocalFS");
        return store;
    }

    @Test
    public void testPostArchive() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":create:"+projectID)).thenReturn(true);
        ArchiveTaskStep step = new ArchiveTaskStep(mockChannel());
        Task task = step.toTask();
        assertThat(getResources().client().resource("/api/1/tasks/archive").type(MediaType.APPLICATION_JSON_TYPE).post(Task.class, step), equalTo(task));
    }

    @Test
    public void testPostCull() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":create:"+projectID)).thenReturn(true);
        CullTaskStep step = new CullTaskStep(mockProject());
        Task task = step.toTask();
        assertThat(getResources().client().resource("/api/1/tasks/cull").type(MediaType.APPLICATION_JSON_TYPE).post(Task.class, step), equalTo(task));
    }

    @Test
    public void testPostDestroy() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":create:"+projectID)).thenReturn(true);
        DestroyTaskStep step = new DestroyTaskStep(UUID.randomUUID().toString(), mockStore());
        Task task = step.toTask();
        assertThat(getResources().client().resource("/api/1/tasks/destroy").type(MediaType.APPLICATION_JSON_TYPE).post(Task.class, step), equalTo(task));
    }

    @Test
    public void testPostDiscover() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":create:"+projectID)).thenReturn(true);
        DiscoverTaskStep step = new DiscoverTaskStep(mockChannel());
        Task task = step.toTask();
        assertThat(getResources().client().resource("/api/1/tasks/discover").type(MediaType.APPLICATION_JSON_TYPE).post(Task.class, step), equalTo(task));
    }

    @Test
    public void testPostExtract() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":create:"+projectID)).thenReturn(true);
        ExtractTaskStep step = new ExtractTaskStep(UUID.randomUUID().toString(), mockStore());
        Task task = step.toTask();
        assertThat(getResources().client().resource("/api/1/tasks/extract").type(MediaType.APPLICATION_JSON_TYPE).post(Task.class, step), equalTo(task));
    }

    @Override
    public void testAuthorizedPost() throws Exception {
        // We override this since it can't work
        // in the test harness
    }

    @Override
    public void testAuthorizedPut() throws Exception {
        // We override this since it can't work
        // in the test harness
    }

    @Override
    public void testAuthorizedDelete() throws Exception {
        // We override this since it can't work
        // in the test harness
    }

    @Override
    public void testUnAuthorizedPost() throws Exception {
        // We override this since it can't work
        // in the test harness
    }

    @Override
    public void testUnAuthorizedPut() throws Exception {
        // We override this since it can't work
        // in the test harness
    }

    @Override
    public void testUnAuthorisedDelete() throws Exception {
        // We override this since it can't work
        // in the test harness
    }

}