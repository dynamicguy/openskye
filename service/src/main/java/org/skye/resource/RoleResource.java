package org.skye.resource;

import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.skye.domain.AttributeDefinition;
import org.skye.domain.Permission;
import org.skye.domain.Role;
import org.skye.domain.RolePermission;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.RoleDAO;
import org.skye.util.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/roles", description = "Manage roles")
@Path("/api/1/roles")
@Produces(MediaType.APPLICATION_JSON)
public class RoleResource extends AbstractUpdatableDomainResource<Role> {

    @Inject
    protected RoleDAO roleDAO;

    @ApiOperation(value = "Create new role", notes = "Create a new Role and return with a unique id", response = Role.class)
    @POST
    @Transactional
    @Timed
    public Role create(Role newInstance){
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
        for(RolePermission rp : role.getRolePermissions()) {
            permissions.add(rp.getPermission());
        }
        return new PaginatedResult<Permission>().paginate(permissions);
    }

}
