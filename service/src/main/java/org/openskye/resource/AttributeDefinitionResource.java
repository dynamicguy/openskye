package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.domain.AttributeDefinition;
import org.openskye.domain.AttributeType;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.AttributeDefinitionDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.BadRequestException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * The REST endpoint for {@link org.openskye.domain.Domain}
 */
@Api(value = "/api/1/attributeDefinitions", description = "Manage attributeDefinitions")
@Path("/api/1/attributeDefinitions")
@Produces(MediaType.APPLICATION_JSON)
public class AttributeDefinitionResource extends AbstractUpdatableDomainResource<AttributeDefinition> {


    protected AttributeDefinitionDAO attributeDefinitionDAO;

    @Inject
    public AttributeDefinitionResource(AttributeDefinitionDAO dao) {
        this.attributeDefinitionDAO = dao;
    }

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
    public AttributeDefinition create(AttributeDefinition newInstance) {
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

    @ApiOperation(value = "List all", notes = "Returns all attribute definitions in a paginated structure", responseContainer = "List", response = AttributeDefinition.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<AttributeDefinition> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete attribute definition", notes = "Deletes the attribute definition (found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
    }

    @Override
    protected void validateUpdate(String id, AttributeDefinition newInstance)
    {
        List<String> possibleValues = newInstance.getPossibleValues();

        if(possibleValues == null)
            possibleValues = new ArrayList<>();

        if(newInstance.getType() != AttributeType.ENUMERATED)
        {
            if(possibleValues.size() != 0)
                throw new BadRequestException("Only Enumerated attributes may have possible values.");

            return;
        }

        for(String value : possibleValues)
        {
            int numberOf = 0;

            for(String otherValue : possibleValues)
            {
                if(otherValue.equals(value))
                    numberOf++;
            }

            if(numberOf > 1)
                throw new BadRequestException("Each possible value for an Enumerated attribute must be unique.");
        }
    }
}
