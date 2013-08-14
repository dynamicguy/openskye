package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.skye.domain.User;
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
@Path("/api/1/auditLogs")
@Produces(MediaType.APPLICATION_JSON)
/**
 * Manage domains
 */
public class AccountResource {

    @Path("/")
    @GET
    @ApiOperation(value = "Based on your login will return the subjects user information")
    public User getAuditLogProperties(@PathParam("id") String id) {
        if (SecurityUtils.getSubject() != null) {
            if (SecurityUtils.getSubject() instanceof User) {
                return (User) SecurityUtils.getSubject();
            } else {
                throw new NotFoundException();
            }

        } else {
            throw new UnauthorizedException();
        }
    }


}
