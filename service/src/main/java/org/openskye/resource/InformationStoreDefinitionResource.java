package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.InformationStoreDefinitionDAO;
import org.openskye.domain.dao.PaginatedResult;

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
public class InformationStoreDefinitionResource extends AbstractUpdatableDomainResource<InformationStoreDefinition> {

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
        if(isPermitted("create",newInstance.getProject().getId())){
            return super.create(newInstance);
        }else {
            throw new UnauthorizedException();
        }
    }

    @ApiOperation(value = "Update information store definition", notes = "Enter the id of the information store definition to update and enter the new information. Returns the updated information store definition", response = InformationStoreDefinition.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public InformationStoreDefinition update(@PathParam("id") String id, InformationStoreDefinition newInstance) {
        if(isPermitted("update",newInstance.getProject().getId())){
            return super.update(id, newInstance);
        }else{
            throw new UnauthorizedException();
        }
    }

    @ApiOperation(value = "Find information store definition by id", notes = "Return an information store definition by its id", response = InformationStoreDefinition.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public InformationStoreDefinition get(@PathParam("id") String id) {
        InformationStoreDefinition result = super.get(id);
        if(isPermitted("get",result.getProject().getId())){
            return result;
        }else{
            throw new UnauthorizedException();
        }
    }

    @ApiOperation(value = "List all", notes = "Returns all information stores definitions in a paginated structure", responseContainer = "List", response = InformationStoreDefinition.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<InformationStoreDefinition> getAll() {
        PaginatedResult<InformationStoreDefinition> paginatedResult = super.getAll();
        List<InformationStoreDefinition> results = paginatedResult.getResults();
        for(InformationStoreDefinition isd : results){
            if(!isPermitted("list",isd.getProject().getId())){
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
        if(isPermitted("delete",id)) {
            return super.delete(id);
        } else{
            throw new UnauthorizedException();
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

    public boolean isPermitted(String action, String projectId) {
        return SecurityUtils.getSubject().isPermitted(getPermissionDomain() + ":" + action + ":" + projectId);
    }
}
