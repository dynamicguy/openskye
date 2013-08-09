package org.skye.resource;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import junit.framework.TestCase;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.Domain;
import org.skye.resource.dao.DomainDAO;
import org.skye.util.PaginatedResult;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DomainResourceTest extends ResourceTest {
    private final Domain domain = new Domain();
    private String id;
    private final DomainDAO dao = mock(DomainDAO.class);
    private final Subject subject = mock(Subject.class);
    private PaginatedResult<Domain> expectedResult = new PaginatedResult<>();

    @Override
    protected void setUpResources() {
        when(dao.list()).thenReturn(expectedResult);
        when(dao.delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(domain);
        when(dao.persist(domain)).thenReturn(domain);
        DomainResource domainResource = new DomainResource();
        domainResource.domainDAO = dao;
        addResource(domainResource);
    }

    @Test
    public void testAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("domain:create")).thenReturn(true);
        assertThat(client().resource("/api/1/domains").type(MediaType.APPLICATION_JSON_TYPE).post(Domain.class, domain)).isEqualTo(domain);
    }

    @Test
    public void testUnAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("domain:create")).thenReturn(false);
        assertEquals(401,client().resource("/api/1/domains").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, domain).getStatus());
    }

    @Test
    public void testAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("domain:list")).thenReturn(true);
        assertThat(client().resource("/api/1/domains").get(PaginatedResult.class)).isEqualTo(expectedResult);
    }

    @Test
    public void testUnAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("domain:list")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/domains").get(PaginatedResult.class)).isEqualTo(expectedResult);
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }

    @Test
    public void testAuthorizedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("domain:delete")).thenReturn(true);
        ClientResponse response = client().resource("/api/1/domains/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertEquals(response.getStatus(),200);

        verify(dao).delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }
    @Test
    public void testUnAuthorisedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("domain:delete")).thenReturn(false);
        ClientResponse response = client().resource("/api/1/domains/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertEquals(401,response.getStatus());
    }
        @Test
    public void testAuthorizedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("domain:get")).thenReturn(true);
        assertThat(client().resource("/api/1/domains/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(Domain.class))
                .isEqualTo(domain);
        verify(dao).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("domain:get")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/domains/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(Domain.class));
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }
}