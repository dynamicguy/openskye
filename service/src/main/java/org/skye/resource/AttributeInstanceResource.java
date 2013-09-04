package org.skye.resource;

import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.skye.domain.AttributeInstance;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.AttributeInstanceDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/attributeInstance", description = "Manage attributeInstances")
@Path("/api/1/attributeInstances")
@Produces(MediaType.APPLICATION_JSON)
public class AttributeInstanceResource extends AbstractUpdatableDomainResource<AttributeInstance> {

    @Inject
    protected AttributeInstanceDAO attributeInstanceDAO;

    @ApiOperation(value = "Create new", notes = "Create a new instance and return with id", response = AttributeInstance.class)
    @POST
    @Transactional
    @Timed
    public AttributeInstance create(AttributeInstance newInstance){
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update instance", notes = "Update the instance", response = AttributeInstance.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public AttributeInstance update(@PathParam("id") String id, AttributeInstance newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find by id", notes = "Return an instance by id", response = AttributeInstance.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public AttributeInstance get(@PathParam("id") String id) {
        return super.get(id);
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
