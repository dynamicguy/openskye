package org.openskye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.openskye.domain.AttributeInstance;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.AttributeInstanceDAO;
import org.openskye.domain.dao.ChannelDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import static org.mockito.Mockito.mock;

public class AttributeInstanceResourceTest extends AbstractResourceTest<AttributeInstance> {

    public static final AttributeInstanceDAO dao = mock(AttributeInstanceDAO.class);

    public static final ChannelDAO channelDAO = mock(ChannelDAO.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AttributeInstanceResource(dao, channelDAO))
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
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