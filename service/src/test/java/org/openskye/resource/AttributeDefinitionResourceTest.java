package org.openskye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.openskye.domain.AttributeDefinition;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.AttributeDefinitionDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import static org.mockito.Mockito.mock;

public class AttributeDefinitionResourceTest extends AbstractResourceTest<AttributeDefinition> {

    public static final AttributeDefinitionDAO dao = mock(AttributeDefinitionDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AttributeDefinitionResource(dao))
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
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