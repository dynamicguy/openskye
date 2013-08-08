package org.skye.resource;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.AuditLog;
import org.skye.resource.dao.AuditLogDAO;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AuditLogResourceTest extends ResourceTest {
    private final AuditLog auditLog = new AuditLog();
    private final AuditLogDAO dao = mock(AuditLogDAO.class);
    private final Subject subject = mock(Subject.class);

    @Override
    protected void setUpResources() {
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(auditLog);
        AuditLogResource auditLogResource = new AuditLogResource();
        auditLogResource.auditLogDAO = dao;
        addResource(auditLogResource);
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