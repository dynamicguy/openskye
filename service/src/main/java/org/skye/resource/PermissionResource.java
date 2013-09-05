package org.skye.resource;

import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.skye.domain.Permission;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.PermissionDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/permissions", description = "Manage permmisions")
@Path("/api/1/permissions")
@Produces(MediaType.APPLICATION_JSON)
/**
 * Manage domains     ..
 */
public class PermissionResource extends AbstractUpdatableDomainResource<Permission> {

    @Inject
    protected PermissionDAO permissionDAO;

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

    @Override
    protected AbstractPaginatingDAO<Permission> getDAO() {
        return permissionDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "permission";
    }


}
