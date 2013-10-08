package org.skye.resource;

import com.google.common.base.Optional;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.ClassRule;
import org.skye.domain.AuditLogProperty;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.AuditLogPropertyDAO;
import org.skye.domain.dao.PaginatedResult;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuditLogPropertyResourceTest extends AbstractResourceTest<AuditLogProperty> {
    private static final AuditLogPropertyDAO dao = mock(AuditLogPropertyDAO.class);
    @ClassRule
    private static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AuditLogPropertyResource(dao))
            .build();
    private static final Subject subject = mock(Subject.class);
    private final AuditLogProperty auditLogProperty = new AuditLogProperty();
    private PaginatedResult<AuditLogProperty> expectedResult = new PaginatedResult<>();

    @Before
    public void setUp() {
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(Optional.of(auditLogProperty));
        when(dao.delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(dao.create(auditLogProperty)).thenReturn(auditLogProperty);
        when(dao.update("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9", auditLogProperty)).thenReturn(auditLogProperty);
    }

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