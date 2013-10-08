package org.skye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.skye.domain.AttributeInstance;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.AttributeInstanceDAO;
import org.skye.domain.dao.PaginatedResult;

import static org.mockito.Mockito.mock;

public class AttributeInstanceResourceTest extends AbstractResourceTest<AttributeInstance> {

    public static final AttributeInstanceDAO dao = mock(AttributeInstanceDAO.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AttributeInstanceResource(dao))
            .build();
    private final AttributeInstance attributeInstance = new AttributeInstance();
    private PaginatedResult<AttributeInstance> expectedResult = new PaginatedResult<>();

    public String getSingular() {
        return "attributeInstance";
    }

    public String getPlural() {
        return "attributeInstances";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public AttributeInstance getInstance() {
        return attributeInstance;
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