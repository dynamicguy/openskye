package org.skye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.skye.domain.User;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.UserDAO;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
/**
 * Manage account information for the currently authorized user
 */
public class AccountResource extends AbstractUpdatableDomainResource<User> {

    @Inject
    private UserDAO userDao;

    @Override
    protected AbstractPaginatingDAO<User> getDAO() {
        return userDao;
    }

    @Override
    protected String getPermissionDomain() {
        return "user";
    }

    @GET
    @ApiOperation(value = "Based on your login will return your API key", response = UserSelf.class)
    @Transactional
    @Timed
    public UserSelf getUserSelf() {
        Subject subject = SecurityUtils.getSubject();
        return new UserSelf((User) subject.getPrincipal());
    }

}
