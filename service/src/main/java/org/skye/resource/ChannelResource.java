package org.skye.resource;

import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.skye.domain.Channel;
import org.skye.domain.ChannelArchiveStore;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.ChannelDAO;
import org.skye.util.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.*;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/channels", description = "Manage channels")
@Path("/api/1/channels")
public class ChannelResource extends AbstractUpdatableDomainResource<Channel> {

    @Inject
    protected ChannelDAO channelDAO;

    @ApiOperation(value = "Create new", notes = "Create a new instance and return with id", response = Channel.class)
    @POST
    @Transactional
    @Timed
    public Channel create(Channel newInstance){
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update instance", notes = "Update the instance", response = Channel.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public Channel update(@PathParam("id") String id, Channel newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find by id", notes = "Return an instance by id", response = Channel.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public Channel get(@PathParam("id") String id) {
        return super.get(id);
    }

    @Override
    protected AbstractPaginatingDAO<Channel> getDAO() {
        return channelDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "channel";
    }

    @Path("/{id}/channelArchiveStores")
    @GET
    @ApiOperation(value = "Return the archive stores used by this channel")
    public PaginatedResult<ChannelArchiveStore> getChannelArchiveStores(@PathParam("id") String id) {
        Channel channel = get(id);
        return new PaginatedResult<ChannelArchiveStore>().paginate(channel.getChannelArchiveStores());

    }
}
