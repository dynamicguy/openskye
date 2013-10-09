package org.skye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.skye.domain.AuditLog;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.AuditLogDAO;
import org.skye.domain.dao.PaginatedResult;

import static org.mockito.Mockito.mock;

public class AuditLogResourceTest extends AbstractResourceTest<AuditLog> {
    public static final AuditLogDAO dao = mock(AuditLogDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AuditLogResource(dao))
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