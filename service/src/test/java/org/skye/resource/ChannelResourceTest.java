package org.skye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.skye.domain.Channel;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.ChannelDAO;
import org.skye.domain.dao.PaginatedResult;

import static org.mockito.Mockito.mock;

public class ChannelResourceTest extends AbstractResourceTest<Channel> {
    private static final ChannelDAO dao = mock(ChannelDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new ChannelResource(dao))
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
    public Object getExpectedResult() {
        return expectedResult;
    }
}