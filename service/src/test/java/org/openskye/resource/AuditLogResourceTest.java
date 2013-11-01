package org.openskye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.openskye.domain.AuditLog;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.AuditLogDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import static org.mockito.Mockito.mock;

public class AuditLogResourceTest extends AbstractResourceTest<AuditLog> {
    public static final AuditLogDAO dao = mock(AuditLogDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AuditLogResource(dao))
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
            .build();
    private final AuditLog auditLog = new AuditLog();
    private PaginatedResult<AuditLog> expectedResult = new PaginatedResult<>();

    @Override
    public String getSingular() {
        return "auditLog";
    }

    @Override
    public String getPlural() {
        return "auditLogs";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public AuditLog getInstance() {
        return auditLog;
    }

    @Override
    public AbstractPaginatingDAO getDAO() {
        return dao;
    }

    @Override
    public PaginatedResult getExpectedResult() {
        return expectedResult;
    }
}