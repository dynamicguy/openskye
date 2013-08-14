package org.skye.resource;

import com.google.common.base.Optional;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.AuditLog;
import org.skye.resource.dao.AuditLogDAO;
import org.skye.util.PaginatedResult;

import javax.ws.rs.core.MediaType;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AuditLogResourceTest extends ResourceTest {
    private final AuditLog auditLog = new AuditLog();
    private final AuditLogDAO dao = mock(AuditLogDAO.class);
    private final Subject subject = mock(Subject.class);
    private PaginatedResult<AuditLog> expectedResult = new PaginatedResult<>();

    @Override
    protected void setUpResources() {
        when(dao.list()).thenReturn(expectedResult);
        when(dao.delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(Optional.of(auditLog));
        when(dao.persist(auditLog)).thenReturn(auditLog);
        AuditLogResource auditLogResource = new AuditLogResource();
        auditLogResource.auditLogDAO = dao;
        addResource(auditLogResource);
    }

    @Test
    public void testAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("auditLog:update")).thenReturn(true);
        assertThat(client().resource("/api/1/auditLogs/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(AuditLog.class, auditLog)).isEqualTo(auditLog);
    }

    @Test
    public void testUnAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("auditLog:update")).thenReturn(false);
        assertEquals(401, client().resource("/api/1/auditLogs/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, auditLog).getStatus());
    }

    @Test
    public void testAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("auditLog:create")).thenReturn(true);
        assertThat(client().resource("/api/1/auditLogs").type(MediaType.APPLICATION_JSON_TYPE).post(AuditLog.class, auditLog)).isEqualTo(auditLog);
    }

    @Test
    public void testUnAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("auditLog:create")).thenReturn(false);
        assertEquals(401, client().resource("/api/1/auditLogs").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, auditLog).getStatus());
    }

    @Test
    public void testAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("auditLog:list")).thenReturn(true);
        assertThat(client().resource("/api/1/auditLogs").get(PaginatedResult.class)).isEqualTo(expectedResult);
    }

    @Test
    public void testUnAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("auditLog:list")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/auditLogs").get(PaginatedResult.class)).isEqualTo(expectedResult);
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }

    @Test
    public void testAuthorizedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("auditLog:delete")).thenReturn(true);
        ClientResponse response = client().resource("/api/1/auditLogs/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertEquals(200, response.getStatus());

        verify(dao).delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("auditLog:delete")).thenReturn(false);
        ClientResponse response = client().resource("/api/1/auditLogs/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertEquals(401, response.getStatus());
    }

    @Test
    public void testAuthorizedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("auditLog:get")).thenReturn(true);
        assertThat(client().resource("/api/1/auditLogs/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(AuditLog.class))
                .isEqualTo(auditLog);
        verify(dao).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("auditLog:get")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/auditLogs/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(AuditLog.class));
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }
}