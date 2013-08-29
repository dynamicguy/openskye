package org.skye.resource;

import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import com.yammer.metrics.annotation.Timed;
import org.apache.shiro.SecurityUtils;
import org.skye.util.UnauthorizedException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * An abstract base for a resource that will present
 * domain objects persisted in hibernate
 */
@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractUpdatableDomainResource<T> extends AbstractRealOnlyDomainResource<T> {

    @ApiOperation(value = "Create new", notes = "Create a new instance and return with id")
    @POST
    @UnitOfWork
    @Timed
    public T create(T newInstance) {
        if (SecurityUtils.getSubject().isPermitted(getPermissionDomain() + ":create")) {
            onCreate(newInstance);
            return getDAO().persist(newInstance);
        } else {
            throw new UnauthorizedException();
        }
    }

    protected void onCreate(T newInstance) {
        // Do nothing by default
    }

    @ApiOperation(value = "Update instance", notes = "Update the instance")
    @Path("/{id}")
    @PUT
    @UnitOfWork
    @Timed
    public T update(@PathParam("id") String id, T newInstance) {
        // TODO need to do the merge here?
        if (SecurityUtils.getSubject().isPermitted(getPermissionDomain() + ":update")) {
            return getDAO().persist(newInstance);
        } else {
            throw new UnauthorizedException();
        }
    }

    @ApiOperation(value = "Delete instance", notes = "Delete the instance")
    @Path("/{id}")
    @DELETE
    @UnitOfWork
    @Timed
    public Response delete(@PathParam("id") String id) {
        if (SecurityUtils.getSubject().isPermitted(getPermissionDomain() + ":delete")) {
            getDAO().delete(id);
            return Response.ok().build();
        } else {
            throw new UnauthorizedException();
        }

    }
}
