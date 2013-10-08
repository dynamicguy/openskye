package org.skye.resource;

import com.google.common.base.Optional;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.skye.domain.AttributeDefinition;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.AttributeDefinitionDAO;
import org.skye.domain.dao.PaginatedResult;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AttributeDefinitionResourceTest extends AbstractResourceTest<AttributeDefinition> {

    private static final AttributeDefinitionDAO dao = mock(AttributeDefinitionDAO.class);
    @ClassRule
    private static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AttributeDefinitionResource(dao))
            .build();
    private final AttributeDefinition attributeDefinition = new AttributeDefinition();
    private PaginatedResult<AttributeDefinition> expectedResult = new PaginatedResult<>();

    @Before
    public void setUp() {
        when(dao.list()).thenReturn(expectedResult);
        when(dao.delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(Optional.of(attributeDefinition));
        when(dao.create(attributeDefinition)).thenReturn(attributeDefinition);
        when(dao.update("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9", attributeDefinition)).thenReturn(attributeDefinition);
        AttributeDefinitionResource attributeDefinitionResource = new AttributeDefinitionResource(dao);
        attributeDefinitionResource.attributeDefinitionDAO = dao;
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
    public AttributeDefinition getInstance() {
        return attributeDefinition;
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