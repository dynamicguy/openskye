package org.openskye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.openskye.domain.AuditLogProperty;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.AuditLogPropertyDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import static org.mockito.Mockito.mock;

public class AuditLogPropertyResourceTest extends AbstractResourceTest<AuditLogProperty> {

    public static final AuditLogPropertyDAO dao = mock(AuditLogPropertyDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AuditLogPropertyResource(dao))
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
            .build();
    private final AuditLogProperty auditLogProperty = new AuditLogProperty();
    private PaginatedResult<AuditLogProperty> expectedResult = new PaginatedResult<>();

    @Override
    public String getSingular() {
        return "auditLogProperty";
    }

    @Override
    public String getPlural() {
        return "auditLogProperties";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public AuditLogProperty getInstance() {
        return auditLogProperty;
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