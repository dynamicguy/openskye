package org.skye.resource;

import com.google.common.base.Optional;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;
import org.skye.domain.User;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.UserDAO;
import org.skye.security.ApiKeyToken;
import org.skye.util.NotFoundException;
import org.skye.util.UnauthorizedException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
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
    @ApiOperation(value = "Based on your login will return your API key", response = String.class)
    @Transactional
    public String getApiKey() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            Object principal = subject.getPrincipal();
            if (principal instanceof User) {
                User user = (User) principal;
                ApiKeyToken token = new ApiKeyToken(user);
                return token.getKey();
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new UnauthorizedException();
        }
    }

}
