package org.openskye.resource;

import com.sun.jersey.api.client.ClientResponse;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.apache.shiro.util.ThreadContext;
import org.junit.ClassRule;
import org.junit.Test;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.ObjectSet;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import javax.ws.rs.core.MediaType;
import java.net.URLEncoder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;


/**
 * A set of tests on the {@link ObjectSetResource}
 */
public class ObjectSetResourceTest extends AbstractObjectTest {

    public static final String API_ADDRESS = "/api/1/objectSets";
    public static final MediaType MEDIA_TYPE = MediaType.APPLICATION_JSON_TYPE;
    @ClassRule
    public static final ResourceTestRule resourceRule = ResourceTestRule.builder()
            .addResource(buildResource())
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
            .build();

    private static ObjectSetResource buildResource() {
        return new ObjectSetResource(repository, search);
    }

    @Test
    public void testAuthorizedCreate() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectSetResource.OPERATION_CREATE)).thenReturn(true);

        String objectSetName = URLEncoder.encode(objectSetInstance.getName(), "UTF-8");

        ObjectSet set = resourceRule.client()
                .resource(API_ADDRESS)
                .type(MEDIA_TYPE)
                .post(ObjectSet.class, objectSetInstance);

        assertThat("a valid object set was created", set.getId(), equalTo(objectSetInstance.getId()));
    }

    @Test
    public void testUnauthorizedCreate() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectSetResource.OPERATION_CREATE)).thenReturn(false);

        ClientResponse response = resourceRule.client()
                .resource(API_ADDRESS)
                .type(MEDIA_TYPE)
                .post(ClientResponse.class, objectSetInstance);

        assertThat("object set creation was not permitted", response.getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorizedGet() {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectSetResource.OPERATION_GET)).thenReturn(true);

        String api = API_ADDRESS + "/" + objectSetInstance.getId();

        ObjectSet objectSet = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .get(ObjectSet.class);

        assertThat("get object set yielded the expected result", objectSet.getId(), equalTo(objectSetInstance.getId()));
    }

    @Test
    public void testUnauthorizedGet() {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectSetResource.OPERATION_GET)).thenReturn(false);

        String api = API_ADDRESS + "/" + objectSetInstance.getId();

        ClientResponse response = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .get(ClientResponse.class);

        assertThat("get object set was not permitted", response.getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorizedGetAll() {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectSetResource.OPERATION_LIST)).thenReturn(true);

        PaginatedResult<ObjectSet> result = resourceRule.client()
                .resource(API_ADDRESS)
                .type(MEDIA_TYPE)
                .get(PaginatedResult.class);

        assertThat("list object sets yielded expected result", result, equalTo(objectSetResult));
    }

    @Test
    public void testUnauthorizedGetAll() {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectSetResource.OPERATION_LIST)).thenReturn(false);

        ClientResponse response = resourceRule.client()
                .resource(API_ADDRESS)
                .type(MEDIA_TYPE)
                .get(ClientResponse.class);

        assertThat("list object sets is not permitted", response.getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorizedDelete() {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectSetResource.OPERATION_DELETE)).thenReturn(true);

        String api = API_ADDRESS + "/" + objectSetInstance.getId();

        ClientResponse response = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .delete(ClientResponse.class);

        assertThat("object set can be deleted", response.getStatus(), equalTo(expectedResponse.getStatus()));
    }

    @Test
    public void testUnauthorizedDelete() {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectSetResource.OPERATION_DELETE)).thenReturn(false);

        String api = API_ADDRESS + "/" + objectSetInstance.getId();

        ClientResponse response = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .delete(ClientResponse.class);

        assertThat("delete object set is not permitted", response.getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorizedIsFound() {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectSetResource.OPERATION_GET)).thenReturn(true);

        String api = API_ADDRESS + "/" +
                objectSetInstance.getId() +
                "/contains/" + metadataInstance.getId();

        Boolean isFound = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .get(Boolean.class);

        assertThat("object is found in object set", isFound, equalTo(Boolean.TRUE));
    }

    @Test
    public void testUnauthorizedIsFound() {

        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectSetResource.OPERATION_GET)).thenReturn(false);

        String api = API_ADDRESS + "/" +
                objectSetInstance.getId() +
                "/contains/" + metadataInstance.getId();

        ClientResponse response = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .get(ClientResponse.class);

        assertThat("object set is found operation is not permitted", response.getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorizedGetObjects() {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectSetResource.OPERATION_GET)).thenReturn(true);

        String api = API_ADDRESS + "/metadata/" +
                objectSetInstance.getId();

        PaginatedResult<ObjectMetadata> result = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .get(PaginatedResult.class);

        assertThat("get objects from set yields expected results", result, equalTo(metadataResult));
    }

    @Test
    public void testUnauthorizedGetObjects() {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectSetResource.OPERATION_GET)).thenReturn(false);

        String api = API_ADDRESS + "/metadata/" +
                objectSetInstance.getId();

        ClientResponse response = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .get(ClientResponse.class);

        assertThat("get objects from set is not permitted", response.getStatus(), equalTo(401));

    }

    @Test
    public void testAuthorizedAddObject() {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectSetResource.OPERATION_ADD)).thenReturn(true);

        String api = API_ADDRESS + "/" +
                objectSetInstance.getId() + "/" +
                metadataInstance.getId();

        ClientResponse response = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .put(ClientResponse.class);

        assertThat("object is added to object set", response.getStatus(), equalTo(expectedResponse.getStatus()));
    }

    @Test
    public void testUnauthorizedAddObject() {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectSetResource.OPERATION_ADD)).thenReturn(false);

        String api = API_ADDRESS + "/" +
                objectSetInstance.getId() + "/" +
                metadataInstance.getId();

        ClientResponse response = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .put(ClientResponse.class);

        assertThat("add object to set is not permitted", response.getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorizedRemoveObject() {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectSetResource.OPERATION_REMOVE)).thenReturn(true);

        String api = API_ADDRESS + "/" +
                objectSetInstance.getId() + "/" +
                metadataInstance.getId();

        ClientResponse response = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .delete(ClientResponse.class);

        assertThat("object is removed from object set", response.getStatus(), equalTo(expectedResponse.getStatus()));
    }

    @Test
    public void testUnauthorizedRemoveObject() {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectSetResource.OPERATION_REMOVE)).thenReturn(false);

        String api = API_ADDRESS + "/" +
                objectSetInstance.getId() + "/" +
                metadataInstance.getId();

        ClientResponse response = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .delete(ClientResponse.class);

        assertThat("remove object from set is not permitted", response.getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorizedAddFromSearch() {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectSetResource.OPERATION_SEARCH)).thenReturn(true);

        String api = API_ADDRESS + "/" +
                objectSetInstance.getId() + "/search/?query=" + pathSearch;

        ObjectSet objectSet = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .put(ObjectSet.class);

        assertThat("objects were added to a set from a search", objectSet.getId(), equalTo(objectSetInstance.getId()));
    }

    @Test
    public void testUnauthorizedAddFromSearch() {
        ThreadContext.bind(subject);
        when(subject.isPermitted(ObjectSetResource.OPERATION_SEARCH)).thenReturn(false);

        String api = API_ADDRESS + "/" +
                objectSetInstance.getId() + "/search/?query=" + pathSearch;

        ClientResponse response = resourceRule.client()
                .resource(api)
                .type(MEDIA_TYPE)
                .put(ClientResponse.class);

        assertThat("add objects to set from search operation is not permitted",
                response.getStatus(),
                equalTo(401));
    }
}
