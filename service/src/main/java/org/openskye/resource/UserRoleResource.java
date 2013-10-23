package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.domain.UserRole;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.UserRoleDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.openskye.domain.UserRole}
 */
@Api(value = "/api/1/userRoles", description = "Manage user roles")
@Path("/api/1/userRoles")
public class UserRoleResource extends AbstractUpdatableDomainResource<UserRole> {

    private UserRoleDAO userRoleDAO;

    @Inject
    public UserRoleResource(UserRoleDAO dao) {
        this.userRoleDAO = dao;
    }

    @ApiOperation(value = "List all user roles", notes = "Returns all user roles in a paginated structure", responseContainer = "List", response = UserRole.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<UserRole> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Add new user role", response = UserRole.class)
    @POST
    @Transactional
    @Timed
    @Override
    public UserRole create(UserRole newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Delete user role", notes = "Delete the user role")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
    }

    @Override
    protected AbstractPaginatingDAO<UserRole> getDAO() {
        return userRoleDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "userRole";
    }

}
