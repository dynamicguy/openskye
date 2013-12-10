package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.domain.Channel;
import org.openskye.domain.ChannelArchiveStore;
import org.openskye.domain.ChannelFilterDefinition;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.ChannelDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.NotFoundException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * The REST endpoint for {@link org.openskye.domain.Domain}
 */
@Api(value = "/api/1/channels", description = "Manage channels")
@Path("/api/1/channels")
public class ChannelResource extends ProjectSpecificResource<Channel> {

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
        projectID = newInstance.getProject().getId();
        super.create(newInstance);
        for (ChannelArchiveStore cas : newInstance.getChannelArchiveStores()) {
            cas.setChannel(newInstance);
        }
        if (newInstance.getChannelFilters().size() > 0) {
            for (ChannelFilterDefinition def : newInstance.getChannelFilters()) {
                def.setChannel(newInstance);
            }
        }
        return newInstance;

    }

    @ApiOperation(value = "Update channel", notes = "Enter the id of the channel to update and new information. Returns the updated channel", response = Channel.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public Channel update(@PathParam("id") String id, Channel newInstance) {
        projectID = newInstance.getProject().getId();
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find chanel by id", notes = "Return a channel by its unique id", response = Channel.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public Channel get(@PathParam("id") String id) {
        projectID="";
        authorize("get");
        if(channelDAO.get(id).isPresent()){
            Channel result = channelDAO.get(id).get();
            projectID=result.getProject().getId();
            return super.get(id);
        } else {
            throw new NotFoundException();
        }
    }

    @ApiOperation(value = "List all channels", notes = "Returns all channels in a paginated structure", responseContainer = "List", response = Channel.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<Channel> getAll() {
        projectID="";
        PaginatedResult<Channel> paginatedResult = super.getAll();
        List<Channel> results = paginatedResult.getResults();
        for (Channel channel : results) {
            if (!isPermitted("list", channel.getProject().getId())) {
                results.remove(channel);
            }
        }
        paginatedResult.setResults(results);
        return paginatedResult;
    }

    @ApiOperation(value = "Delete channel", notes = "Deletes the channel(found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        if(channelDAO.get(id).isPresent()){
            Channel result = channelDAO.get(id).get();
            projectID=result.getProject().getId();
            return super.delete(id);
        } else {
            throw new NotFoundException();
        }
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
        if(channelDAO.get(id).isPresent()){
            Channel channel = channelDAO.get(id).get();
            projectID=channel.getProject().getId();
            authorize("get");
            return new PaginatedResult<ChannelArchiveStore>().paginate(channel.getChannelArchiveStores());
        } else {
            throw new NotFoundException();
        }
    }
}
