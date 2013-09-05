package org.skye.resource;

import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.skye.domain.AttributeDefinition;
import org.skye.domain.RetentionPolicy;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.RetentionPolicyDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
    public RetentionPolicy create(RetentionPolicy newInstance){
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

    @Override
    protected AbstractPaginatingDAO<RetentionPolicy> getDAO() {
        return retentionPolicyDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "retentionPolicy";
    }


}
