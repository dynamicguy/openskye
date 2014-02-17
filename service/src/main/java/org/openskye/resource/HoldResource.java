package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.openskye.domain.Hold;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.HoldDAO;
import org.openskye.domain.dao.PaginatedResult;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Random;

/**
 * REST API endpoint for a {@link Hold}
 */

@Api(value = "/api/1/holds", description = "Access and manage object holds")
@Path("/api/1/holds")
@Slf4j
public class HoldResource extends AbstractUpdatableDomainResource<Hold> {

    protected HoldDAO holdDAO;

    @Inject
    public HoldResource(HoldDAO dao){
        this.holdDAO=dao;
    }

    @ApiOperation(value = "Create new hold", notes = "Create a new hold and return with its unique id", response = Hold.class)
    @POST
    @Transactional
    @Timed
    @Override
    public Hold create(Hold newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update instance", notes = "Find the hold to update by id and enter the new information. Returns updated hold", response = Hold.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public Hold update(@PathParam("id") String id, Hold newHold) {
        return super.update(id, newHold);
    }

    @ApiOperation(value = "Find hold by id", notes = "Return a hold by their unique id", response = Hold.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public Hold get(@PathParam("id") String id) {
        return super.get(id);
    }

    @ApiOperation(value = "List all holds", notes = "Returns all holds in a paginated structure", responseContainer = "List", response = Hold.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<Hold> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete hold", notes = "Delete the hold (found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
    }

    @Override
    protected AbstractPaginatingDAO<Hold> getDAO() {
        return holdDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "hold";
    }

    @ApiOperation(value = "Create a hold based on a query")
    @Path("/holdQuery")
    @POST
    @Transactional
    @Timed
    public Hold createFromQuery(@QueryParam("query") String query){
        Hold newHold = new Hold();
        newHold.setQuery(query);
        newHold.setName("Unnamed Hold No. "+ new Random().nextInt(100));
        newHold.setDescription(query);
        return create(newHold);
    }
}
