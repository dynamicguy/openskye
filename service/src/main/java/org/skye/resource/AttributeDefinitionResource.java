package org.skye.resource;

import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.skye.domain.AttributeDefinition;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.AttributeDefinitionDAO;
import org.skye.util.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/attributeDefinitions", description = "Manage attributeDefinitions")
@Path("/api/1/attributeDefinitions")
@Produces(MediaType.APPLICATION_JSON)
public class AttributeDefinitionResource extends AbstractUpdatableDomainResource<AttributeDefinition> {

    @Inject
    protected AttributeDefinitionDAO attributeDefinitionDAO;

    @ApiOperation(value = "Update attribute definition", notes = "Enter the id of the attribute definition to update and enter the new information, returns updated attribute definition", response = AttributeDefinition.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public AttributeDefinition update(@PathParam("id") String id, AttributeDefinition newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Create new attribute definition", notes = "Create a new attribute definition and return with its unique id", response = AttributeDefinition.class)
    @POST
    @Transactional
    @Timed
    public AttributeDefinition create(AttributeDefinition newInstance){
        return super.create(newInstance);
    }

    @ApiOperation(value = "Find attribute definition by id", notes = "Return an attribute definition by searching for its id", response = AttributeDefinition.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public AttributeDefinition get(@PathParam("id") String id) {
        return super.get(id);
    }

    @Override
    protected AbstractPaginatingDAO<AttributeDefinition> getDAO() {
        return attributeDefinitionDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "attributeDefinition";
    }


}
