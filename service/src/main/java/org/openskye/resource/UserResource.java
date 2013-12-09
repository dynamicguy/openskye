package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.core.SkyeException;
import org.openskye.domain.User;
import org.openskye.domain.UserRole;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.UserDAO;
import org.openskye.exceptions.BadRequestException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.openskye.domain.User}
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
    public User update(@PathParam("id") String id, UpdateUser userUpdate) {
        authorize("update");

        // We need to merge the user that we have in the system
        // with the one that is coming in to handle password changes

        User user = get(id);
        user.setEmail(userUpdate.getEmail());
        user.setName(userUpdate.getName());

        // Check if we have a password
        if (userUpdate.getOldPassword() != null) {
            if (userUpdate.getNewPassword() == null || userUpdate.getNewPassword().trim().equals("")) {
                throw new BadRequestException("You need to provide a new password");
            }
            if (!user.isPassword(userUpdate.getOldPassword())) {
                throw new BadRequestException("Your old password doesn't match");
            }
            user.setPassword(userUpdate.getNewPassword());
            user.encryptPassword();
        }

        validateUpdate(id, user);
        return getDAO().update(id, user);
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

    @ApiOperation(value = "Deactivate a user", notes = "Given the user ID, deactivates the user by removing their API key (they can be reactivated again by assigning a new API key", response = User.class)
    @Path("/deactivate/{id}")
    @PUT
    @Transactional
    @Timed
    public User deactivateUser(@PathParam("id") String userID) {
        Optional<User> userOpt = userDAO.get(userID);
        if (userOpt.isPresent()) {
            User currentUser = userOpt.get();

            userDAO.update(currentUser);
            return currentUser;
        } else {
            throw new SkyeException("User not found");
        }
    }
}
