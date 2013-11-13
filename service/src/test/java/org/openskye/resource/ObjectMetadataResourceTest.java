package org.openskye.resource;

import com.google.common.base.Optional;
import com.sun.jersey.api.client.ClientResponse;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.junit.ClassRule;
import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.ObjectMetadata;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests the ObjectMetadataResource and the ObjectSetResource.
 */
public class ObjectMetadataResourceTest extends AbstractObjectTest
{
    @ClassRule
    public static final ResourceTestRule resourceRule = ResourceTestRule.builder()
                                                                        .addResource(buildResource())
                                                                        .addProvider(new AuthorizationExceptionMapper())
                                                                        .addProvider(new AuthenticationExceptionMapper())
                                                                        .build();

    public static final String API_ADDRESS = "/api/1/objects";
    public static final MediaType MEDIA_TYPE = MediaType.APPLICATION_JSON_TYPE;

    private static ObjectMetadataResource buildResource()
    {
        ObjectMetadataResource resource = new ObjectMetadataResource(repository, search);

        resource.setDomainDAO(domains);
        resource.setInformationStoreDefinitionDAO(informationStores);
        resource.setProjectDAO(projects);
        resource.setStoreRegistry(registry);
        resource.setTaskDAO(tasks);

        return resource;
    }

    @Test
    public void testAuthorizedCreate()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_CREATE)).thenReturn(true);

        // Since create generates a new UUID before attempting to put something into the repository,
        // it is impossible to assert to ensure that the instance that is written is the same as the
        // instance we passed in.
        ObjectMetadata metadata = resourceRule.client()
                                              .resource(API_ADDRESS)
                                              .type(MEDIA_TYPE)
                                              .post(ObjectMetadata.class, metadataInstance);

        assertThat("an object metadata was created", (metadata != null));

    }

    @Test
    public void testUnauthorizedCreate()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_CREATE)).thenReturn(false);

        ClientResponse response = resourceRule.client()
                                              .resource(API_ADDRESS)
                                              .type(MEDIA_TYPE)
                                              .post(ClientResponse.class, metadataInstance);

        assertThat("object creation was not permitted", response.getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorizedUpdate()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_UPDATE)).thenReturn(true);

        String api = API_ADDRESS + "/" + metadataInstance.getId();

        // In this case, the two ids should match.
        ObjectMetadata metadata = resourceRule.client()
                                              .resource(api)
                                              .type(MEDIA_TYPE)
                                              .put(ObjectMetadata.class, metadataInstance);

        assertThat("object was updated", metadata.getId(), equalTo(metadataInstance.getId()));
    }

    @Test
    public void testUnauthorizedUpdate()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_UPDATE)).thenReturn(false);

        String api = API_ADDRESS + "/" + metadataInstance.getId();

        ClientResponse response = resourceRule.client()
                                              .resource(api)
                                              .type(MEDIA_TYPE)
                                              .put(ClientResponse.class, metadataInstance);

        assertThat("object update was not permitted", response.getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorizedGet()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_GET)).thenReturn(true);

        String api = API_ADDRESS + "/" + metadataInstance.getId();

        ObjectMetadata metadata = resourceRule.client()
                                              .resource(api)
                                              .type(MEDIA_TYPE)
                                              .get(ObjectMetadata.class);

        assertThat("object was correctly retrieved", metadata.getId(), equalTo(metadataInstance.getId()));
    }

    @Test
    public void testUnauthorizedGet()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_GET)).thenReturn(false);

        String api = API_ADDRESS + "/" + metadataInstance.getId();

        ClientResponse response  = resourceRule.client()
                                               .resource(api)
                                               .type(MEDIA_TYPE)
                                               .get(ClientResponse.class);

        assertThat("get object was not permitted", response.getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorizedList()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_LIST)).thenReturn(true);

        PaginatedResult<ObjectMetadata> result = resourceRule.client()
                                                             .resource(API_ADDRESS)
                                                             .type(MEDIA_TYPE)
                                                             .get(PaginatedResult.class);

        assertThat("repository lists expected results", result, equalTo(metadataResult));
    }

    @Test
    public void testUnauthorizedList()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_LIST)).thenReturn(false);

        ClientResponse response = resourceRule.client()
                                              .resource(API_ADDRESS)
                                              .type(MEDIA_TYPE)
                                              .get(ClientResponse.class);

        assertThat("list operation was not permitted", response.getStatus(), equalTo(401));
    }

    /**
    @Test
    public void testAuthorizedGetContent()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_GET)).thenReturn(true);

        String api = API_ADDRESS + "/" + metadataInstance.getId() + "/content";

        ClientResponse response = resourceRule.client()
                                              .resource(api)
                                              .type(MEDIA_TYPE)
                                              .get(ClientResponse.class);

        assertThat("get content was successful", response.getStatus(), equalTo(expectedResponse.getStatus()));
    }
    **/

    @Test
    public void testUnauthorizedGetContent()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_GET)).thenReturn(false);

        String api = API_ADDRESS + "/" + metadataInstance.getId() + "/content";

        ClientResponse response = resourceRule.client()
                                              .resource(api)
                                              .type(MEDIA_TYPE)
                                              .get(ClientResponse.class);

        assertThat("get content was not permitted", response.getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorizedGetContentBlocks()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_GET)).thenReturn(true);

        String api = API_ADDRESS + "/" +
                     metadataInstance.getId() +
                     "/blocks";

        PaginatedResult<ArchiveContentBlock> result = resourceRule.client()
                                                                  .resource(api)
                                                                  .type(MEDIA_TYPE)
                                                                  .get(PaginatedResult.class);

        assertThat("get archive content blocks yeilds expected result", result, equalTo(acbResult));
    }

    @Test
    public void testUnauthorizedGetContentBlocks()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_GET)).thenReturn(false);

        String api = API_ADDRESS + "/" +
                metadataInstance.getId() +
                "/blocks";

        ClientResponse response = resourceRule.client()
                                              .resource(api)
                                              .type(MEDIA_TYPE)
                                              .get(ClientResponse.class);

        assertThat("get archive content blocks was not permitted", response.getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorizedGetByInformationStore()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_LIST)).thenReturn(true);

        String api = API_ADDRESS + "/informationStore/" + isdSearch.getId();

        PaginatedResult<ObjectMetadata> result = resourceRule.client()
                                                             .resource(api)
                                                             .type(MEDIA_TYPE)
                                                             .get(PaginatedResult.class);

        assertThat("list by information store yields expected result", result, equalTo(metadataByIsdResult));
    }

    @Test
    public void testUnauthorizedGetByInformationStore()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_LIST)).thenReturn(false);

        String api = API_ADDRESS + "/informationStore/" + isdSearch.getId();

        ClientResponse response = resourceRule.client()
                                              .resource(api)
                                              .type(MEDIA_TYPE)
                                              .get(ClientResponse.class);

        assertThat("list by information store is not permitted", response.getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorizedGetByTask()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_LIST)).thenReturn(true);

        String api = API_ADDRESS + "/task/" + taskSearch.getId();

        PaginatedResult<ObjectMetadata> result = resourceRule.client()
                                                             .resource(api)
                                                             .type(MEDIA_TYPE)
                                                             .get(PaginatedResult.class);

        assertThat("list by task yields expected result", result, equalTo(metadataByTaskResult));
    }

    @Test
    public void testUnauthorizedGetByTask()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_LIST)).thenReturn(false);

        String api = API_ADDRESS + "/task/" + taskSearch.getId();

        ClientResponse response = resourceRule.client()
                                              .resource(api)
                                              .type(MEDIA_TYPE)
                                              .get(ClientResponse.class);

        assertThat("list by task is not permitted", response.getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorizedIndex()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_INDEX)).thenReturn(true);

        String api = API_ADDRESS + "/index/" + metadataInstance.getId();

        ClientResponse response = resourceRule.client()
                                              .resource(api)
                                              .type(MEDIA_TYPE)
                                              .put(ClientResponse.class);

        assertThat("object was indexed", response.getStatus(), equalTo(expectedResponse.getStatus()));
    }

    @Test
    public void testUnauthorizedIndex()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_INDEX)).thenReturn(false);

        String api = API_ADDRESS + "/index/" + metadataInstance.getId();

        ClientResponse response = resourceRule.client()
                                              .resource(api)
                                              .type(MEDIA_TYPE)
                                              .put(ClientResponse.class);

        assertThat("index operation was not permitted", response.getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorizedSearch()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_SEARCH)).thenReturn(true);

        String api = API_ADDRESS + "/search/" + session.getDomain().getId() + "?query=" + pathSearch;

        PaginatedResult<ObjectMetadata> result = resourceRule.client()
                                                             .resource(api)
                                                             .type(MEDIA_TYPE)
                                                             .get(PaginatedResult.class);

        assertThat("search returned expected results", result, equalTo(metadataSearchResult));
    }

    @Test
    public void testUnauthorizedSearch()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_SEARCH)).thenReturn(false);

        String api = API_ADDRESS + "/search/" + session.getDomain().getId() + "?query=" + pathSearch;

        ClientResponse response = resourceRule.client()
                                              .resource(api)
                                              .type(MEDIA_TYPE)
                                              .get(ClientResponse.class);

        assertThat("search was not permitted", response.getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorizedSearchProject()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_SEARCH)).thenReturn(true);

        String api = API_ADDRESS + "/search/" +
                     session.getDomain().getId() + "/" +
                     projectSearch.getId() +
                     "?query=" + pathSearch;

        PaginatedResult<ObjectMetadata> result = resourceRule.client()
                                                             .resource(api)
                                                             .type(MEDIA_TYPE)
                                                             .get(PaginatedResult.class);

        assertThat("search with project returned expected results", result, equalTo(metadataSearchResult));
    }

    @Test
    public void testUnauthorizedSearchProject()
    {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectMetadataResource.OPERATION_SEARCH)).thenReturn(false);

        String api = API_ADDRESS + "/search/" +
                     session.getDomain().getId() + "/" +
                     projectSearch.getId() +
                     "?query=" + pathSearch;

        ClientResponse response = resourceRule.client()
                                              .resource(api)
                                              .type(MEDIA_TYPE)
                                              .get(ClientResponse.class);

        assertThat("search with project was not permitted", response.getStatus(), equalTo(401));
    }
}
