package org.skye.resource;

import org.apache.shiro.SecurityUtils;
import org.skye.domain.Identifiable;
import org.skye.util.UnauthorizedException;

import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * An abstract base for a resource that will present
 * domain objects persisted in hibernate
 */
@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractUpdatableDomainResource<T extends Identifiable> extends AbstractRealOnlyDomainResource<T>
{
    public T create(T newInstance) {
        if (SecurityUtils.getSubject().isPermitted(getPermissionDomain() + ":create")) {
            return getDAO().create(newInstance);
        } else {
            throw new UnauthorizedException();
        }
    }

    public T update(@PathParam("id") String id, T newInstance) {
        if (SecurityUtils.getSubject().isPermitted(getPermissionDomain() + ":update")) {
            return getDAO().update(id, newInstance);
        } else {
            throw new UnauthorizedException();
        }
    }

    public Response delete(@PathParam("id") String id) {
        if (SecurityUtils.getSubject().isPermitted(getPermissionDomain() + ":delete")) {
            getDAO().delete(id);
            return Response.ok().build();
        } else {
            throw new UnauthorizedException();
        }

    }
}
