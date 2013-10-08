package org.skye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.skye.domain.RetentionPolicy;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.PaginatedResult;
import org.skye.domain.dao.RetentionPolicyDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/retentionPolicies", description = "Manage retentionPolicies")
@Path("/api/1/retentionPolicies")
@Produces(MediaType.APPLICATION_JSON)
public class RetentionPolicyResource extends AbstractUpdatableDomainResource<RetentionPolicy> {

    @Inject
    protected RetentionPolicyDAO retentionPolicyDAO;

    @ApiOperation(value = "Create new retention policy", notes = "Create a new retention policy and return with its unique id", response = RetentionPolicy.class)
    @POST
    @Transactional
    @Timed
    public RetentionPolicy create(RetentionPolicy newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update retention policy", notes = "Update the retention policy by id, returns the updated policy", response = RetentionPolicy.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public RetentionPolicy update(@PathParam("id") String id, RetentionPolicy newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find retention policy by id", notes = "Return retention policy by its unique id", response = RetentionPolicy.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public RetentionPolicy get(@PathParam("id") String id) {
        return super.get(id);
    }

    @ApiOperation(value = "List all retention policies", notes = "Returns all retention policies in a paginated structure", responseContainer = "List", response = RetentionPolicy.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<RetentionPolicy> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete retention policy", notes = "Deletes the retention policy(found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
    }

    @Override
    protected AbstractPaginatingDAO<RetentionPolicy> getDAO() {
        return retentionPolicyDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "retentionPolicy";
    }


}
