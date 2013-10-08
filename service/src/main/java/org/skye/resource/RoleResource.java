package org.skye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.skye.domain.Permission;
import org.skye.domain.Role;
import org.skye.domain.RolePermission;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.PaginatedResult;
import org.skye.domain.dao.RoleDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/roles", description = "Manage roles")
@Path("/api/1/roles")
@Produces(MediaType.APPLICATION_JSON)
public class RoleResource extends AbstractUpdatableDomainResource<Role> {

    private RoleDAO roleDAO;

    @Inject
    public RoleResource(RoleDAO dao) {
        this.roleDAO = dao;
    }

    @ApiOperation(value = "Create new role", notes = "Create a new Role and return with a unique id", response = Role.class)
    @POST
    @Transactional
    @Timed
    public Role create(Role newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update role", notes = "Find a role by id and enter new information. Return updated role information", response = Role.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public Role update(@PathParam("id") String id, Role newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find role by id", notes = "Return a role by its unique id", response = Role.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public Role get(@PathParam("id") String id) {
        return super.get(id);
    }

    @ApiOperation(value = "List all roles", notes = "Returns all roles in a paginated structure", responseContainer = "List", response = Role.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<Role> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete role", notes = "Deletes the role(found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
    }

    @Override
    protected AbstractPaginatingDAO<Role> getDAO() {
        return roleDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "role";
    }

    @Path("/{id}/permissions")
    @GET
    @ApiOperation(value = "Return the permissions for this role")
    public PaginatedResult<Permission> getPermissions(@PathParam("id") String id) {
        Role role = get(id);
        List<Permission> permissions = new ArrayList<>();
        for (RolePermission rp : role.getRolePermissions()) {
            permissions.add(rp.getPermission());
        }
        return new PaginatedResult<Permission>().paginate(permissions);
    }

}
