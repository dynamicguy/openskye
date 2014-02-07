package org.openskye.resource;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.apache.shiro.util.ThreadContext;
import org.junit.ClassRule;
import org.junit.Test;
import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.ObjectMetadata;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Tests the ObjectMetadataResource and the ObjectSetResource.
 */
public class ObjectMetadataResourceTest extends AbstractObjectTest {
    @ClassRule
    public static final ResourceTestRule resourceRule = ResourceTestRule.builder()
            .addResource(buildResource())
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
            .build();
    public static final String API_ADDRESS = "/api/1/objects";
    public static final MediaType MEDIA_TYPE = MediaType.APPLICATION_JSON_TYPE;

    private static ObjectMetadataResource buildResource() {
        return new ObjectMetadataResource(repository, search, informationStores, domains, tasks, projects);
    }

    @Test
    public void testAuthorizedUpdate() {
        ThreadContext.bind(subject);
        when(subject.isPermitted("objects:update:"+metadataInstance.getProject().getId())).thenReturn(true);

        String api = API_ADDRESS + "/" + metadataInstance.getId();

        // In this case, the two ids should match.
        ObjectMetadata metadata = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .put(ObjectMetadata.class, metadataInstance);

        assertThat("object was updated", metadata.getId(), equalTo(metadataInstance.getId()));
    }

    @Test
    public void testUnauthorizedUpdate() {
        ThreadContext.bind(subject);
        when(subject.isPermitted("objects:update"+metadataInstance.getProject().getId())).thenReturn(false);

        String api = API_ADDRESS + "/" + metadataInstance.getId();

        ClientResponse response = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .put(ClientResponse.class, metadataInstance);

        assertThat("object update was not permitted", response.getStatus(), equalTo(403));
    }

    @Test
    public void testAuthorizedGet() {
        ThreadContext.bind(subject);
        when(subject.isPermitted("objects:get:"+metadataInstance.getProject().getId())).thenReturn(true);

        String api = API_ADDRESS + "/" + metadataInstance.getId();

        ObjectMetadata metadata = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .get(ObjectMetadata.class);

        assertThat("object was correctly retrieved", metadata.getId(), equalTo(metadataInstance.getId()));
    }

    @Test
    public void testUnauthorizedGet() {
        ThreadContext.bind(subject);
        when(subject.isPermitted("objects:get"+metadataInstance.getProject().getId())).thenReturn(false);

        String api = API_ADDRESS + "/" + metadataInstance.getId();

        ClientResponse response = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .get(ClientResponse.class);

        assertThat("get object was not permitted", response.getStatus(), equalTo(403));
    }

    @Test
    public void testAuthorizedList() {
        ThreadContext.bind(subject);
        when(subject.isPermitted("objects:list:"+anyString())).thenReturn(true);

        PaginatedResult<ObjectMetadata> result = resourceRule.client()
                .resource(API_ADDRESS)
                .type(MEDIA_TYPE)
                .get(PaginatedResult.class);
        assertThat("repository lists expected results", result, equalTo(metadataResult));
    }

    @Test
    public void testUnauthorizedList() {
        ThreadContext.bind(subject);
        when(subject.isPermitted("objects:list:"+anyString())).thenReturn(false);

        ClientResponse response = resourceRule.client()
                .resource(API_ADDRESS)
                .type(MEDIA_TYPE)
                .get(ClientResponse.class);

        assertThat("list operation was not permitted", response.getStatus(), equalTo(403));
    }

    @Test
    public void testAuthorizedGetContentBlocks() {
        ThreadContext.bind(subject);
        when(subject.isPermitted("objects:get:"+metadataInstance.getProject().getId())).thenReturn(true);

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
    public void testUnauthorizedGetContentBlocks() {
        ThreadContext.bind(subject);
        when(subject.isPermitted("objects:get:"+metadataInstance.getProject().getId())).thenReturn(false);

        String api = API_ADDRESS + "/" +
                metadataInstance.getId() +
                "/blocks";

        ClientResponse response = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .get(ClientResponse.class);

        assertThat("get archive content blocks was not permitted", response.getStatus(), equalTo(403));
    }

    @Test
    public void testAuthorizedGetByInformationStore() {
        ThreadContext.bind(subject);
        when(subject.isPermitted("objects:list:"+isdSearch.getProject().getId())).thenReturn(true);

        String api = API_ADDRESS + "/informationStore/" + isdSearch.getId();

        PaginatedResult<ObjectMetadata> result = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .get(PaginatedResult.class);

        assertThat("list by information store yields expected result", result, equalTo(metadataByIsdResult));
    }

    @Test
    public void testUnauthorizedGetByInformationStore() {
        ThreadContext.bind(subject);
        when(subject.isPermitted("objects:list:"+isdSearch.getProject().getId())).thenReturn(false);

        String api = API_ADDRESS + "/informationStore/" + isdSearch.getId();

        ClientResponse response = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .get(ClientResponse.class);

        assertThat("list by information store is not permitted", response.getStatus(), equalTo(403));
    }

    @Test
    public void testAuthorizedGetByTask() {
        ThreadContext.bind(subject);
        when(subject.isPermitted("objects:list:"+taskSearch.getProject().getId())).thenReturn(true);

        String api = API_ADDRESS + "/task/" + taskSearch.getId();

        PaginatedResult<ObjectMetadata> result = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .get(PaginatedResult.class);

        assertThat("list by task yields expected result", result, equalTo(metadataByTaskResult));
    }

    @Test
    public void testUnauthorizedGetByTask() {
        ThreadContext.bind(subject);
        when(subject.isPermitted("objects:list:"+taskSearch.getProject().getId())).thenReturn(false);

        String api = API_ADDRESS + "/task/" + taskSearch.getId();

        ClientResponse response = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .get(ClientResponse.class);

        assertThat("list by task is not permitted", response.getStatus(), equalTo(403));
    }

    @Test
    public void testAuthorizedIndex() {
        ThreadContext.bind(subject);
        when(subject.isPermitted("objects:index:"+metadataInstance.getProject().getId())).thenReturn(true);

        String api = API_ADDRESS + "/index/" + metadataInstance.getId();

        ClientResponse response = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .put(ClientResponse.class);

        assertThat("object was indexed", response.getStatus(), equalTo(expectedResponse.getStatus()));
    }

    @Test
    public void testUnauthorizedIndex() {
        ThreadContext.bind(subject);
        when(subject.isPermitted("objects:index:"+metadataInstance.getProject().getId())).thenReturn(false);

        String api = API_ADDRESS + "/index/" + metadataInstance.getId();

        ClientResponse response = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .put(ClientResponse.class);

        assertThat("index operation was not permitted", response.getStatus(), equalTo(403));
    }

    @Test
    public void testAuthorizedSearch() {
        ThreadContext.bind(subject);
        when(subject.isPermitted("objects:search:"+anyString())).thenReturn(true);


        String api = API_ADDRESS + "/search/?query=" + pathSearch;

        WebResource resource = resourceRule.client().resource(api);

        PaginatedResult<ObjectMetadata> result = resource.type(MEDIA_TYPE).get(PaginatedResult.class);
        result.setPageSize(12);
        result.setTotalResults(12);

        assertThat("search returned expected results", result, equalTo(metadataSearchResult));
    }

    @Test
    public void testUnauthorizedSearch() {
        ThreadContext.bind(subject);
        when(subject.isPermitted("objects:search:"+metadataSearchResult.getResults().get(0).getProject().getId())).thenReturn(false);

        String api = API_ADDRESS + "/search/?query=" + pathSearch;

        WebResource resource = resourceRule.client().resource(api);

        ClientResponse response = resource.type(MEDIA_TYPE).get(ClientResponse.class);

        assertThat("search was not permitted", response.getStatus(), equalTo(403));
    }

    @Test
    public void testAuthorizedSearchProject() {
        ThreadContext.bind(subject);
        when(subject.isPermitted("objects:search:"+projectSearch.getId())).thenReturn(true);

        String api = API_ADDRESS + "/search/" +
                projectSearch.getId() +
                "?query=" + pathSearch;

        WebResource resource = resourceRule.client().resource(api);

        PaginatedResult<ObjectMetadata> result = resource.type(MEDIA_TYPE).get(PaginatedResult.class);
        result.setPageSize(12);
        result.setTotalResults(12);

        assertThat("search with project returned expected results", result, equalTo(metadataSearchResult));
    }

    @Test
    public void testUnauthorizedSearchProject() {
        ThreadContext.bind(subject);
        when(subject.isPermitted("objects:search:"+projectSearch.getId())).thenReturn(false);

        String api = API_ADDRESS + "/search/" +
                projectSearch.getId() +
                "?query=" + pathSearch;

        WebResource resource = resourceRule.client().resource(api);

        ClientResponse response = resource.type(MEDIA_TYPE).get(ClientResponse.class);

        assertThat("search with project was not permitted", response.getStatus(), equalTo(403));
    }
}
