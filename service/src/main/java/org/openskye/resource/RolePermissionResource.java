package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.domain.Project;
import org.openskye.domain.RolePermission;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.RolePermissionDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(value="/api/1/rolePermissions", description="Manage Role Permissions")
@Path("/api/1/rolePermissions")
@Produces(MediaType.APPLICATION_JSON)
public class RolePermissionResource extends AbstractUpdatableDomainResource<RolePermission>
{
    private RolePermissionDAO rolePermissionDAO;

    @Inject
    public RolePermissionResource(RolePermissionDAO injectedDao)
    {
        rolePermissionDAO = injectedDao;
    }

    @Override
    public AbstractPaginatingDAO<RolePermission> getDAO()
    {
        return rolePermissionDAO;
    }

    @Override
    protected String getPermissionDomain()
    {
        return "rolePermissions";
    }

    @ApiOperation(value = "Create new role permission",
                  notes = "Create a new role permission and return with its unique id",
                  response = RolePermission.class)
    @POST
    @Transactional
    @Timed
    public RolePermission create(RolePermission newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Find role permission by id",
                  notes = "Return a role permission by its unique id",
                  response = RolePermission.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public RolePermission get(@PathParam("id") String id) {
        return super.get(id);
    }

    @ApiOperation(value = "List all role permissions",
                  notes = "Returns all role permissions in a paginated structure",
                  responseContainer = "List",
                  response = RolePermission.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<RolePermission> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete role permission",
                  notes = "Deletes the role permission(found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
    }
}
