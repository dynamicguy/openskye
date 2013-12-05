package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.core.SkyeException;
import org.openskye.domain.Role;
import org.openskye.domain.User;
import org.openskye.domain.UserRole;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.RoleDAO;
import org.openskye.domain.dao.UserDAO;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for an administrator to perform various functions on other users
 */

@Api(value = "/api/1/administrator", description = "Perform administrative functions on users and their permissions")
@Path("/api/1/administrator")
@Produces(MediaType.APPLICATION_JSON)
public class AdministratorResource extends AbstractUpdatableDomainResource{
    @Inject
    private UserDAO userDAO;

    @Inject
    private RoleDAO roleDAO;

    @Override
    protected AbstractPaginatingDAO getDAO() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected String getPermissionDomain() {
        return "administrator";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @ApiOperation(value = "Set the role of the given user (by ID) to the given role type (also by ID)", notes = "Find the user to update by id and enter the ID of the new role. Returns updated user information", response = User.class)
    @Path("/{id}/set/{roleID}")
    @PUT
    @Transactional
    @Timed
    public User setRole(@PathParam("id") String userID, @PathParam("roleID") String roleID){
        Optional<User> userOpt = userDAO.get(userID);
        Optional<Role> roleOpt = roleDAO.get(roleID);

        if(userOpt.isPresent() && roleOpt.isPresent()){
            User currentUser = userOpt.get();
            Role newRole = roleOpt.get();
            UserRole userRole = new UserRole();
            userRole.setUser(currentUser);
            userRole.setRole(newRole);
            currentUser.addRole(userRole);
            userDAO.update(currentUser);
            return currentUser;
        } else{
            throw new SkyeException("Missing API parameters");
        }
    }

    @ApiOperation(value = "Set the role of the given user (by ID) to the given role type (also by ID)", notes = "Find the user to update by id and enter the ID of the new role. Returns updated user information", response = User.class)
    @Path("/{id}/remove/{roleID}")
    @PUT
    @Transactional
    @Timed
    public User removeRole(@PathParam("id") String userID, @PathParam("roleID") String roleID){
        Optional<User> userOpt = userDAO.get(userID);
        Optional<Role> roleOpt = roleDAO.get(roleID);
        if(userOpt.isPresent() && roleOpt.isPresent()){
            User currentUser = userOpt.get();
            Role newRole = roleOpt.get();
            UserRole userRole = new UserRole();
            userRole.setUser(currentUser);
            userRole.setRole(newRole);
            if(currentUser.getUserRoles().contains(userRole)){
                currentUser.removeRole(userRole);
                userDAO.update(currentUser);
                return currentUser;
            }
            else{
                throw new SkyeException("Role already deleted");
            }
        } else{
            throw new SkyeException("User not found");
        }
    }

    @ApiOperation(value="Reset the password for a given user", notes="Given the user ID, resets their password to a dummy password of the administrator's choosing", response = User.class)
    @Path("/passwordReset/{id}")
    @PUT
    @Transactional
    @Timed
    public User resetPassword(@PathParam("id") String userID, String dummyPwd){
        Optional<User> userOpt = userDAO.get(userID);
        if(userOpt.isPresent()){
            User currentUser = userOpt.get();
            currentUser.setPassword(dummyPwd);
            userDAO.update(currentUser);
            return currentUser;
        }else{
            throw new SkyeException("User not found.");
        }
    }

    @ApiOperation(value="Deactivate a user", notes="Given the user ID, deactivates the user by removing their API key (they can be reactivated again by assigning a new API key", response = User.class)
    @Path("/deactivate/{id}")
    @PUT
    @Transactional
    @Timed
    public User deactivateUser(@PathParam("id") String userID){
        Optional<User> userOpt = userDAO.get(userID);
        if(userOpt.isPresent()){
            User currentUser = userOpt.get();
            currentUser.setApiKey("");
            userDAO.update(currentUser);
            return currentUser;
        } else {
            throw new SkyeException("User not found");
        }
    }
}
