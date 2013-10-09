package org.skye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.skye.domain.User;
import org.skye.domain.UserRole;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.PaginatedResult;
import org.skye.domain.dao.UserDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.skye.domain.User}
 */
@Api(value = "/api/1/users", description = "Manage users")
@Path("/api/1/users")
public class UserResource extends AbstractUpdatableDomainResource<User> {

    private UserDAO userDAO;

    @Inject
    public UserResource(UserDAO dao) {
        this.userDAO = dao;
    }

    @ApiOperation(value = "Create new user", notes = "Create a new user and return with its unique id", response = User.class)
    @POST
    @Transactional
    @Timed
    @Override
    public User create(User newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update instance", notes = "Find the user to update by id and enter the new information. Returns updated user information", response = User.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public User update(@PathParam("id") String id, User newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find user by id", notes = "Return a user by their unique id", response = User.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public User get(@PathParam("id") String id) {
        return super.get(id);
    }

    @Override
    protected AbstractPaginatingDAO<User> getDAO() {
        return userDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "user";
    }

    @Path("/{id}/roles")
    @GET
    @ApiOperation(value = "Return the roles for this user")
    public PaginatedResult<UserRole> getUserRoles(@PathParam("id") String id) {
        User user = get(id);
        return new PaginatedResult<UserRole>().paginate(user.getUserRoles());
    }

    @ApiOperation(value = "List all users", notes = "Returns all users in a paginated structure", responseContainer = "List", response = User.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<User> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete user", notes = "Delete the user (found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
    }
}
