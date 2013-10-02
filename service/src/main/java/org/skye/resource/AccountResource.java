package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;
import org.skye.domain.User;
import org.skye.security.ApiKeyToken;
import org.skye.util.NotFoundException;
import org.skye.util.UnauthorizedException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/account", description = "Access and manage your account information")
@Path("/api/1/account")
@Produces(MediaType.APPLICATION_JSON)
/**
 * Manage account information for the currently authorized user
 */
public class AccountResource {

    @GET
    @ApiOperation(value = "Based on your login will return the subjects user information", response = User.class)
    public User getAuditLogProperties() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            Object principal = subject.getPrincipal();
            if (principal instanceof User) {
                return (User) principal;
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new UnauthorizedException();
        }
    }

    @GET
    @Path("/key")
    @ApiOperation(value = "Based on your login will generate a time-limited API key", response = User.class)
    public String getApiKey() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            Object principal = subject.getPrincipal();
            if (principal instanceof User) {
                User user = (User) principal;
                return new ApiKeyToken(user).getKey();
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new UnauthorizedException();
        }
    }

    @GET
    @Path("/key-eternal")
    @ApiOperation(value = "Based on your login will generate an API key with no time limit", response = User.class)
    public String getEternalApiKey() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            Object principal = subject.getPrincipal();
            if (principal instanceof User) {
                User user = (User) principal;
                return new ApiKeyToken(user,0L).getKey();
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new UnauthorizedException();
        }
    }
}
