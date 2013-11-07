package org.openskye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.openskye.domain.Channel;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.ChannelDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import static org.mockito.Mockito.mock;

public class ChannelResourceTest extends AbstractResourceTest<Channel> {

    public static final ChannelDAO dao = mock(ChannelDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new ChannelResource(dao))
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
            .build();
    private final Channel channel = new Channel();
    private PaginatedResult<Channel> expectedResult = new PaginatedResult<>();

    @Override
    public String getSingular() {
        return "channel";
    }

    @Override
    public String getPlural() {
        return "channels";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public Channel getInstance() {
        return channel;
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