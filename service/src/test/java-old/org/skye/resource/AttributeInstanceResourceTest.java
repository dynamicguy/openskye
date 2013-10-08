package org.skye.resource;

import com.google.common.base.Optional;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.skye.domain.AttributeInstance;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.AttributeInstanceDAO;
import org.skye.domain.dao.PaginatedResult;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AttributeInstanceResourceTest extends AbstractResourceTest<AttributeInstance> {
    private static final AttributeInstanceDAO dao = mock(AttributeInstanceDAO.class);
    @ClassRule
    private static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AttributeInstanceResource(dao))
            .build();
    private final AttributeInstance attributeInstance = new AttributeInstance();
    private PaginatedResult<AttributeInstance> expectedResult = new PaginatedResult<>();

    @Before
    public void setUp() {
        when(dao.list()).thenReturn(expectedResult);
        when(dao.delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(Optional.of(attributeInstance));
        when(dao.create(attributeInstance)).thenReturn(attributeInstance);
        when(dao.update("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9", attributeInstance)).thenReturn(attributeInstance);

        AttributeInstanceResource attributeInstanceResource = new AttributeInstanceResource(dao);
        attributeInstanceResource.attributeInstanceDAO = dao;
    }

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
    public Object getExpectedResult() {
        return expectedResult;
    }


}