package org.openskye.resource;

import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.codahale.metrics.annotation.Timed;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.ArchiveStoreDefinitionDAO;
import org.openskye.domain.dao.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.openskye.domain.Domain}
 */
@Api(value = "/api/1/archiveStoreDefinitions", description = "Manage archive store definitions")
@Path("/api/1/archiveStoreDefinitions")
@Produces(MediaType.APPLICATION_JSON)
public class ArchiveStoreDefinitionResource extends AbstractUpdatableDomainResource<ArchiveStoreDefinition> {

    @Inject
    protected ArchiveStoreDefinitionDAO archiveStoreDefinitionDAO;

    @ApiOperation(value = "Create new archive store definition", notes = "Create a new archive store definition and return with its unique id", response = ArchiveStoreDefinition.class)
    @POST
    @Transactional
    @Timed
    public ArchiveStoreDefinition create(ArchiveStoreDefinition newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update archive store definition", notes = "Enter the id of the archive store definition to update and enter the new information. Returns the updated archive store definition", response = ArchiveStoreDefinition.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public ArchiveStoreDefinition update(@PathParam("id") String id, ArchiveStoreDefinition newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find archive store definition by id", notes = "Return an archive store definition by its id", response = ArchiveStoreDefinition.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public ArchiveStoreDefinition get(@PathParam("id") String id) {
        return super.get(id);
    }

    @ApiOperation(value = "List all", notes = "Returns all archive stores definitions in a paginated structure", responseContainer = "List", response = ArchiveStoreDefinition.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<ArchiveStoreDefinition> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete archive store definition instance", notes = "Deletes the archive store definition instance (found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
    }

    @Override
    protected AbstractPaginatingDAO<ArchiveStoreDefinition> getDAO() {
        return archiveStoreDefinitionDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "archiveStoreDefinition";
    }


}
