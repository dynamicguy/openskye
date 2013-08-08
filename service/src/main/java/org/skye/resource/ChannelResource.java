package org.skye.resource;

import org.skye.domain.Channel;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.ChannelDAO;

import javax.inject.Inject;
import javax.ws.rs.Path;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Path("/api/1/channels")
public class ChannelResource extends AbstractUpdatableDomainResource<Channel> {

    @Inject
    private ChannelDAO channelDAO;

    @Override
    protected AbstractPaginatingDAO<Channel> getDAO() {
        return channelDAO;
    }
}
