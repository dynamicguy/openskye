package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.skye.domain.Permission;
import org.skye.domain.Role;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.RoleDAO;
import org.skye.util.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/roles", description = "Manage roles")
@Path("/api/1/roles")
@Produces(MediaType.APPLICATION_JSON)
/**
 * Manage domains
 */
public class RoleResource extends AbstractUpdatableDomainResource<Role> {

    @Inject
    protected RoleDAO roleDAO;

    @Override
    protected AbstractPaginatingDAO<Role> getDAO() {
        return roleDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "role";
    }

    @Path("/{id}/archiveStores")
    @GET
    @ApiOperation(value = "Return the archive stores owned by this domain")
    public PaginatedResult<Permission> getPermissions(@PathParam("id") String id) {
        Role role = get(id);
        return new PaginatedResult<Permission>().paginate(role.getPermissions());
    }

}
