package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.ArchiveStoreDefinitionDAO;
import org.openskye.domain.dao.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * The REST endpoint for {@link ArchiveStoreDefinition}
 */
@Api(value = "/api/1/archiveStoreDefinitions", description = "Manage archive store definitions")
@Path("/api/1/archiveStoreDefinitions")
@Produces(MediaType.APPLICATION_JSON)
public class ArchiveStoreDefinitionResource extends AbstractUpdatableDomainResource<ArchiveStoreDefinition> {

    protected ArchiveStoreDefinitionDAO archiveStoreDefinitionDAO;

    @Inject
    public ArchiveStoreDefinitionResource(ArchiveStoreDefinitionDAO dao) {
        this.archiveStoreDefinitionDAO = dao;
    }

    @ApiOperation(value = "Create new archive store definition", notes = "Create a new archive store definition and return with its unique id", response = ArchiveStoreDefinition.class)
    @POST
    @Transactional
    @Timed
    public ArchiveStoreDefinition create(ArchiveStoreDefinition newInstance) {
        String projectId = newInstance.getProject().getId();
        if (isPermitted("create", projectId)) {
            return super.create(newInstance);
        } else {
            throw new UnauthorizedException();
        }
    }

    @ApiOperation(value = "Update archive store definition", notes = "Enter the id of the archive store definition to update and enter the new information. Returns the updated archive store definition", response = ArchiveStoreDefinition.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public ArchiveStoreDefinition update(@PathParam("id") String id, ArchiveStoreDefinition newInstance) {
        String projectId = newInstance.getProject().getId();
        if (isPermitted("update", projectId)) {
            return super.update(id, newInstance);
        } else {
            throw new UnauthorizedException();
        }
    }

    @ApiOperation(value = "Find archive store definition by id", notes = "Return an archive store definition by its id", response = ArchiveStoreDefinition.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public ArchiveStoreDefinition get(@PathParam("id") String id) {
        ArchiveStoreDefinition result = super.get(id);
        if(isPermitted("get",result.getProject().getId())){
            return result;
        }
        else{
            throw new UnauthorizedException();
        }
    }

    @ApiOperation(value = "List all", notes = "Returns all archive stores definitions in a paginated structure", responseContainer = "List", response = ArchiveStoreDefinition.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<ArchiveStoreDefinition> getAll() {
        PaginatedResult<ArchiveStoreDefinition> paginatedResult = super.getAll();
        List<ArchiveStoreDefinition> results = paginatedResult.getResults();
        for(ArchiveStoreDefinition asd : results){
            if(!isPermitted("list",asd.getProject().getId())){
                results.remove(asd);
            }
        }
        paginatedResult.setResults(results);
        return paginatedResult;
    }

    @ApiOperation(value = "Delete archive store definition instance", notes = "Deletes the archive store definition instance (found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        ArchiveStoreDefinition definition = super.get(id);
        if(isPermitted("delete",definition.getProject().getId())){
            return super.delete(id);
        }else{
            throw new UnauthorizedException();
        }
    }

    @Override
    protected AbstractPaginatingDAO<ArchiveStoreDefinition> getDAO() {
        return archiveStoreDefinitionDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "archiveStoreDefinition";
    }

    public boolean isPermitted(String action, String projectId) {
        return SecurityUtils.getSubject().isPermitted(getPermissionDomain() + ":" + action + ":" + projectId);
    }

}
