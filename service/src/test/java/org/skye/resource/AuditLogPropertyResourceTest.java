package org.skye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.skye.domain.AuditLogProperty;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.AuditLogPropertyDAO;
import org.skye.domain.dao.PaginatedResult;

import static org.mockito.Mockito.mock;

public class AuditLogPropertyResourceTest extends AbstractResourceTest<AuditLogProperty> {
    private static final AuditLogPropertyDAO dao = mock(AuditLogPropertyDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AuditLogPropertyResource(dao))
            .build();
    private final AuditLogProperty auditLogProperty = new AuditLogProperty();
    private PaginatedResult<AuditLogProperty> expectedResult = new PaginatedResult<>();

    @Override
    public String getSingular() {
        return "attributeDefinition";
    }

    @Override
    public String getPlural() {
        return "attributeDefinitions";
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
    public Object getExpectedResult() {
        return expectedResult;
    }
}