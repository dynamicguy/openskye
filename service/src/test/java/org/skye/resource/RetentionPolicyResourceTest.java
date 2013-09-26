package org.skye.resource;

import com.google.common.base.Optional;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.RetentionPolicy;
import org.skye.domain.dao.PaginatedResult;
import org.skye.domain.dao.RetentionPolicyDAO;

import javax.ws.rs.core.MediaType;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class RetentionPolicyResourceTest extends ResourceTest {
    private final RetentionPolicy retentionPolicy = new RetentionPolicy();
    private final RetentionPolicyDAO dao = mock(RetentionPolicyDAO.class);
    private final Subject subject = mock(Subject.class);
    private PaginatedResult<RetentionPolicy> expectedResult = new PaginatedResult<>();

    @Override
    protected void setUpResources() {
        when(dao.list()).thenReturn(expectedResult);
        when(dao.delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(Optional.of(retentionPolicy));
        when(dao.create(retentionPolicy)).thenReturn(retentionPolicy);
        when(dao.update("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9", retentionPolicy)).thenReturn(retentionPolicy);
        RetentionPolicyResource retentionPolicyResource = new RetentionPolicyResource();
        retentionPolicyResource.retentionPolicyDAO = dao;
        addResource(retentionPolicyResource);
    }

    @Test
    public void testAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("retentionPolicy:update")).thenReturn(true);
        assertThat(client().resource("/api/1/retentionPolicies/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(RetentionPolicy.class, retentionPolicy)).isEqualTo(retentionPolicy);
    }

    @Test
    public void testUnAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("retentionPolicy:update")).thenReturn(false);
        assertEquals(401, client().resource("/api/1/retentionPolicies/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, retentionPolicy).getStatus());
    }

    @Test
    public void testAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("retentionPolicy:create")).thenReturn(true);
        assertThat(client().resource("/api/1/retentionPolicies").type(MediaType.APPLICATION_JSON_TYPE).post(RetentionPolicy.class, retentionPolicy)).isEqualTo(retentionPolicy);
    }

    @Test
    public void testUnAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("retentionPolicy:create")).thenReturn(false);
        assertEquals(401, client().resource("/api/1/retentionPolicies").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, retentionPolicy).getStatus());
    }

    @Test
    public void testAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("retentionPolicy:list")).thenReturn(true);
        assertThat(client().resource("/api/1/retentionPolicies").get(PaginatedResult.class)).isEqualTo(expectedResult);
    }

    @Test
    public void testUnAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("retentionPolicy:list")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/retentionPolicies").get(PaginatedResult.class)).isEqualTo(expectedResult);
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }

    @Test
    public void testAuthorizedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("retentionPolicy:delete")).thenReturn(true);
        ClientResponse response = client().resource("/api/1/retentionPolicies/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertThat(response.getStatus()).isEqualTo(200);

        verify(dao).delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("retentionPolicy:delete")).thenReturn(false);
        ClientResponse response = client().resource("/api/1/retentionPolicies/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    public void testAuthorizedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("retentionPolicy:get")).thenReturn(true);
        assertThat(client().resource("/api/1/retentionPolicies/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(RetentionPolicy.class))
                .isEqualTo(retentionPolicy);
        verify(dao).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("retentionPolicy:get")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/retentionPolicies/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(RetentionPolicy.class));
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }
}