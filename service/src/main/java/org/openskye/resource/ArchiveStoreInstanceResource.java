package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.domain.ArchiveStoreInstance;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.ArchiveStoreInstanceDAO;
import org.openskye.domain.dao.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.openskye.domain.Domain}
 */
@Api(value = "/api/1/archiveStoreInstances", description = "Manage archive store instances")
@Path("/api/1/archiveStoreInstances")
@Produces(MediaType.APPLICATION_JSON)
public class ArchiveStoreInstanceResource extends AbstractUpdatableDomainResource<ArchiveStoreInstance> {

    protected ArchiveStoreInstanceDAO archiveStoreInstanceDAO;

    @Inject
    public ArchiveStoreInstanceResource(ArchiveStoreInstanceDAO dao){
        this.archiveStoreInstanceDAO=dao;
    }

    @ApiOperation(value = "Create new archive store instance", notes = "Create a new archive store instance and return with its unique id", response = ArchiveStoreInstance.class)
    @POST
    @Transactional
    @Timed
    public ArchiveStoreInstance create(ArchiveStoreInstance newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update archive store instance", notes = "Enter the id of the archive store instance to update and enter the new information. Returns the updated archive store instance", response = ArchiveStoreInstance.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public ArchiveStoreInstance update(@PathParam("id") String id, ArchiveStoreInstance newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find archive store instance by id", notes = "Return an archive store instance by its id", response = ArchiveStoreInstance.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public ArchiveStoreInstance get(@PathParam("id") String id) {
        return super.get(id);
    }

    @ApiOperation(value = "List all", notes = "Returns all archive stores instances in a paginated structure", responseContainer = "List", response = ArchiveStoreInstance.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<ArchiveStoreInstance> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete archive store instance", notes = "Deletes the archive store instance (found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
    }

    @Override
    protected AbstractPaginatingDAO<ArchiveStoreInstance> getDAO() {
        return archiveStoreInstanceDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "archiveStoreInstance";
    }


}
