package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.openskye.domain.User;
import org.openskye.domain.dao.UserDAO;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for accessing the logged in user's information
 * <p/>
 * This is a good endpoint for getting information about who is logged in,
 * also when you use this endpoint you see all the user's information about themselves
 */
@Api(value = "/api/1/account", description = "Access and manage your account information")
@Path("/api/1/account")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class AccountResource {

    @Inject
    private UserDAO userDao;

    @GET
    @ApiOperation(value = "Based on your login will return your API key", response = UserSelf.class)
    @Timed
    public UserSelf getUserSelf() {
        Subject subject = SecurityUtils.getSubject();
        return new UserSelf((User) subject.getPrincipal());
    }

    @PUT
    @ApiOperation(value = "This will allow you to update yourself", response = UserSelf.class)
    @Transactional
    @Timed
    @Path("/{id}")
    public UserSelf updateSelf(@PathParam("id") String id, UpdateUser updateUser) {
        Subject subject = SecurityUtils.getSubject();
        User activeUser = (User) subject.getPrincipal();
        Optional<User> user = userDao.get(activeUser.getId());
        if (!activeUser.getId().equals(id) || !user.isPresent()) {
            throw new EntityNotFoundException();
        } else {
            User userToUpdate = user.get();
            userToUpdate.setEmail(updateUser.getEmail());
            userToUpdate.setName(updateUser.getName());
            log.debug("Updating active user to " + userToUpdate);
            userDao.update(userToUpdate);
            return new UserSelf(userToUpdate);
        }

    }

    @ApiOperation(value = "Determine if user has a privilege",
            notes = "For the current user and the permission name, " +
                    "returns true if the permission is associated with the user or false otherwise.",
            response = Boolean.class)
    @Path("/permission/{permission}")
    @GET
    @Timed
    public Boolean isPermitted(@PathParam("permission") String permission) {
        return SecurityUtils.getSubject().isPermitted(permission);
    }

}
