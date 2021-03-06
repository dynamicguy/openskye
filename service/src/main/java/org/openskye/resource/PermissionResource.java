package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.domain.Permission;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.PermissionDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.openskye.domain.Domain}
 */
@Api(value = "/api/1/permissions", description = "Manage permissions")
@Path("/api/1/permissions")
@Produces(MediaType.APPLICATION_JSON)
/**
 * Manage domains     ..
 */
public class PermissionResource extends AbstractUpdatableDomainResource<Permission> {

    protected PermissionDAO permissionDAO;

    @Inject
    public PermissionResource(PermissionDAO dao) {
        this.permissionDAO = dao;
    }

    @ApiOperation(value = "Create new permission", notes = "Create a new permission and return with its id", response = Permission.class)
    @POST
    @Transactional
    @Timed
    public Permission create(Permission newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update permission", notes = "Enter the id of the permission to update and new information. Returns the updated permission", response = Permission.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public Permission update(@PathParam("id") String id, Permission newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find permission by id", notes = "Return a permission by its id", response = Permission.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public Permission get(@PathParam("id") String id) {
        return super.get(id);
    }

    @ApiOperation(value = "List all permissions", notes = "Returns all permissions in a paginated structure", responseContainer = "List", response = Permission.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<Permission> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete permission", notes = "Deletes the permission(found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
    }

    @Override
    protected AbstractPaginatingDAO<Permission> getDAO() {
        return permissionDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "permission";
    }


}
