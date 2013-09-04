package org.skye.resource;

import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.skye.domain.AttributeDefinition;
import org.skye.domain.User;
import org.skye.domain.UserRole;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.UserDAO;
import org.skye.util.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.*;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/users", description = "Manage users")
@Path("/api/1/users")
public class UserResource extends AbstractUpdatableDomainResource<User> {

    @Inject
    protected UserDAO userDAO;

    @ApiOperation(value = "Create new", notes = "Create a new instance and return with id", response = User.class)
    @POST
    @Transactional
    @Timed
    @Override
    public User create(User newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update instance", notes = "Update the instance", response = User.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public User update(@PathParam("id") String id, User newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find by id", notes = "Return an instance by id", response = User.class)
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

}
