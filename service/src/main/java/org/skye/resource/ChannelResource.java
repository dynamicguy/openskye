package org.skye.resource;

import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.skye.domain.Channel;
import org.skye.domain.ChannelArchiveStore;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.ChannelDAO;
import org.skye.domain.dao.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/channels", description = "Manage channels")
@Path("/api/1/channels")
public class ChannelResource extends AbstractUpdatableDomainResource<Channel> {

    @Inject
    protected ChannelDAO channelDAO;

    @ApiOperation(value = "Create new channel", notes = "Create a new channel and return with its unique id", response = Channel.class)
    @POST
    @Transactional
    @Timed
    public Channel create(Channel newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update channel", notes = "Enter the id of the channel to update and new information. Returns the updated channel", response = Channel.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public Channel update(@PathParam("id") String id, Channel newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find chanel by id", notes = "Return a channel by its unique id", response = Channel.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public Channel get(@PathParam("id") String id) {
        return super.get(id);
    }

    @ApiOperation(value = "List all channels", notes = "Returns all channels in a paginated structure", responseContainer = "List", response = Channel.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<Channel> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete channel", notes = "Deletes the channel(found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
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
