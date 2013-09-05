package org.skye.resource;

import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.apache.shiro.SecurityUtils;
import org.skye.util.UnauthorizedException;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * An abstract base for a resource that will present
 * domain objects persisted in hibernate
 */
@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractUpdatableDomainResource<T> extends AbstractRealOnlyDomainResource<T> {

    public T create(T newInstance) {
        if (SecurityUtils.getSubject().isPermitted(getPermissionDomain() + ":create")) {
            return getDAO().persist(newInstance);
        } else {
            throw new UnauthorizedException();
        }
    }

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
    @Transactional
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
