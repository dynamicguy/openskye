package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.domain.Channel;
import org.openskye.domain.ChannelArchiveStore;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.ChannelDAO;
import org.openskye.domain.dao.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.openskye.domain.Domain}
 */
@Api(value = "/api/1/channels", description = "Manage channels")
@Path("/api/1/channels")
public class ChannelResource extends AbstractUpdatableDomainResource<Channel> {

    private ChannelDAO channelDAO;

    @Inject
    public ChannelResource(ChannelDAO dao) {
        this.channelDAO = dao;
    }

    @ApiOperation(value = "Create new channel", notes = "Create a new channel and return with its unique id", response = Channel.class)
    @POST
    @Transactional
    @Timed
    public Channel create(Channel newInstance) {
        super.create(newInstance);
        for(ChannelArchiveStore cas : newInstance.getChannelArchiveStores()){
            cas.setChannel(newInstance);
        }
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
