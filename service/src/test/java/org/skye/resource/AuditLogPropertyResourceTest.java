package org.skye.resource;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import junit.framework.TestCase;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.AuditLogProperty;
import org.skye.domain.Role;
import org.skye.resource.dao.AuditLogPropertyDAO;
import org.skye.resource.dao.RoleDAO;
import org.skye.util.PaginatedResult;

import javax.ws.rs.core.MediaType;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AuditLogPropertyResourceTest extends ResourceTest {
    private final AuditLogProperty auditLogProperty = new AuditLogProperty();
    private final AuditLogPropertyDAO dao = mock(AuditLogPropertyDAO.class);
    private final Subject subject = mock(Subject.class);
    private PaginatedResult<AuditLogProperty> expectedResult = new PaginatedResult<>();

    @Override
    protected void setUpResources() {
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(auditLogProperty);
        when(dao.delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(dao.persist(auditLogProperty)).thenReturn(auditLogProperty);
        AuditLogPropertyResource auditLogPropertyResource = new AuditLogPropertyResource();
        auditLogPropertyResource.auditLogPropertyDAO = dao;
        addResource(auditLogPropertyResource);
    }
    @Test
    public void testAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("auditLogProperty:create")).thenReturn(true);
        assertThat(client().resource("/api/1/auditLogProperties").type(MediaType.APPLICATION_JSON_TYPE).post(AuditLogProperty.class, auditLogProperty)).isEqualTo(auditLogProperty);
    }

    @Test
    public void testUnAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("auditLogProperty:create")).thenReturn(false);
        assertEquals(401,client().resource("/api/1/auditLogProperties").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, auditLogProperty).getStatus());
    }
    @Test
    public void testAuthorizedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("auditLogProperty:delete")).thenReturn(true);
        ClientResponse response = client().resource("/api/1/auditLogProperties/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertEquals(200,response.getStatus());

        verify(dao).delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }
    @Test
    public void testUnAuthorisedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("auditLogProperty:delete")).thenReturn(false);
        ClientResponse response = client().resource("/api/1/auditLogProperties/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertEquals(401,response.getStatus());
    }

    @Test
    public void testAuthorizedGetAll() throws Exception {
        when(dao.list()).thenReturn(expectedResult);
        ThreadContext.bind(subject);
        when(subject.isPermitted("auditLogProperty:list")).thenReturn(true);
        assertThat(client().resource("/api/1/auditLogProperties").get(PaginatedResult.class)).isEqualTo(expectedResult);
    }

    @Test
    public void testUnAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("auditLogProperty:list")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/auditLogProperties").get(PaginatedResult.class)).isEqualTo(expectedResult);
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }


    @Test
    public void testAuthorizedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("auditLogProperty:get")).thenReturn(true);
        assertThat(client().resource("/api/1/auditLogProperties/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(AuditLogProperty.class))
                .isEqualTo(auditLogProperty);
        verify(dao).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("auditLogProperty:get")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/auditLogProperties/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(AuditLogProperty.class));
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }
}