package org.skye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.skye.domain.AttributeDefinition;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.AttributeDefinitionDAO;
import org.skye.domain.dao.PaginatedResult;

import static org.mockito.Mockito.mock;

public class AttributeDefinitionResourceTest extends AbstractResourceTest<AttributeDefinition> {

    public static final AttributeDefinitionDAO dao = mock(AttributeDefinitionDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AttributeDefinitionResource(dao))
            .build();
    private final AttributeDefinition attributeDefinition = new AttributeDefinition();
    private PaginatedResult<AttributeDefinition> expectedResult = new PaginatedResult<>();

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
    public PaginatedResult getExpectedResult() {
        return expectedResult;
    }
}