package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.InformationStoreDefinitionDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.NotFoundException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * The REST endpoint for {@link org.openskye.domain.Domain}
 */
@Api(value = "/api/1/informationStoreDefinitions", description = "Manage information store definitions")
@Path("/api/1/informationStoreDefinitions")
@Produces(MediaType.APPLICATION_JSON)
public class InformationStoreDefinitionResource extends ProjectSpecificResource<InformationStoreDefinition> {

    protected InformationStoreDefinitionDAO informationStoreDefinitionDAO;

    @Inject
    public InformationStoreDefinitionResource(InformationStoreDefinitionDAO dao) {
        this.informationStoreDefinitionDAO = dao;
    }

    @ApiOperation(value = "Create new information store definition", notes = "Create a new information store definition and return with its unique id", response = InformationStoreDefinition.class)
    @POST
    @Transactional
    @Timed
    public InformationStoreDefinition create(InformationStoreDefinition newInstance) {
        projectID = newInstance.getProject().getId();
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update information store definition", notes = "Enter the id of the information store definition to update and enter the new information. Returns the updated information store definition", response = InformationStoreDefinition.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public InformationStoreDefinition update(@PathParam("id") String id, InformationStoreDefinition newInstance) {
        projectID = newInstance.getProject().getId();
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find information store definition by id", notes = "Return an information store definition by its id", response = InformationStoreDefinition.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public InformationStoreDefinition get(@PathParam("id") String id) {
        projectID="";
        authorize("get");
        if(informationStoreDefinitionDAO.get(id).isPresent()){
            InformationStoreDefinition result = informationStoreDefinitionDAO.get(id).get();
            projectID=result.getProject().getId();
            return result;
        } else {
            throw new NotFoundException();
        }
    }

    @ApiOperation(value = "List all", notes = "Returns all information stores definitions in a paginated structure", responseContainer = "List", response = InformationStoreDefinition.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<InformationStoreDefinition> getAll() {
        projectID="";
        PaginatedResult<InformationStoreDefinition> paginatedResult = super.getAll();
        List<InformationStoreDefinition> results = paginatedResult.getResults();
        for (InformationStoreDefinition isd : results) {
            if (!isPermitted("list", isd.getProject().getId())) {
                results.remove(isd);
            }
        }
        paginatedResult.setResults(results);
        return paginatedResult;
    }

    @ApiOperation(value = "Delete information store definition instance", notes = "Deletes the information store definition instance (found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        if(informationStoreDefinitionDAO.get(id).isPresent()){
            InformationStoreDefinition result = informationStoreDefinitionDAO.get(id).get();
            projectID=result.getProject().getId();
            return super.delete(id);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    protected AbstractPaginatingDAO<InformationStoreDefinition> getDAO() {
        return informationStoreDefinitionDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "informationStoreDefinition";
    }
}
