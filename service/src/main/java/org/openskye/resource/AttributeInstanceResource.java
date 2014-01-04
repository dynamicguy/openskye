package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.domain.AttributeInstance;
import org.openskye.domain.Channel;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.AttributeInstanceDAO;
import org.openskye.domain.dao.ChannelDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.NotFoundException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.openskye.domain.Domain}
 */
@Api(value = "/api/1/attributeInstances", description = "Manage attributeInstances")
@Path("/api/1/attributeInstances")
@Produces(MediaType.APPLICATION_JSON)
public class AttributeInstanceResource extends AbstractUpdatableDomainResource<AttributeInstance> {

    protected AttributeInstanceDAO attributeInstanceDAO;

    protected ChannelDAO channelDAO;

    @Inject
    public AttributeInstanceResource(AttributeInstanceDAO dao, ChannelDAO injectedChannelDAO) {
        this.attributeInstanceDAO = dao;
        this.channelDAO = injectedChannelDAO;
    }

    @ApiOperation(value = "Create new attribute", notes = "Create a new attribute and return with its unique id", response = AttributeInstance.class)
    @POST
    @Transactional
    @Timed
    public AttributeInstance create(AttributeInstance newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update attribute", notes = "Enter the id of the attribute to update and enter the new information. Returns the updated attribute", response = AttributeInstance.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public AttributeInstance update(@PathParam("id") String id, AttributeInstance newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find attribute by id", notes = "Return an attribute by its id", response = AttributeInstance.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public AttributeInstance get(@PathParam("id") String id) {
        return super.get(id);
    }

    @ApiOperation(value = "List all", notes = "Returns all attribute definitions in a paginated structure", responseContainer = "List", response = AttributeInstance.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<AttributeInstance> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete attribute instance", notes = "Deletes the attribute instance (found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
    }

    @ApiOperation(value = "Get instances for a channel",
                  notes = "Given a channel, returns a paginated structure containing all defined attribute instances",
                  responseContainer = "List",
                  response = AttributeInstance.class)
    @Path("/channel/{channelId}")
    @GET
    @Transactional
    @Timed
    public PaginatedResult<AttributeInstance> getInstances(@PathParam("channelId") String channelId)
    {
        Optional<Channel> channel = channelDAO.get(channelId);

        if(!channel.isPresent())
        {
            throw new NotFoundException();
        }

        Iterable<AttributeInstance> attributeInstances = attributeInstanceDAO.getInstances(channel.get());

        return new PaginatedResult<>(attributeInstances);
    }

    @Override
    protected AbstractPaginatingDAO<AttributeInstance> getDAO() {
        return attributeInstanceDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "attributeInstance";
    }


}
