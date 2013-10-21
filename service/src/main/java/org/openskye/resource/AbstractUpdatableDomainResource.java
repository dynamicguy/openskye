package org.openskye.resource;

import org.apache.shiro.SecurityUtils;
import org.openskye.domain.Identifiable;
import org.openskye.util.UnauthorizedException;

import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * An abstract base for a resource that will present
 * domain objects persisted in hibernate
 */
@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractUpdatableDomainResource<T extends Identifiable> extends AbstractRealOnlyDomainResource<T> {

    // validate*() should throw BadRequestException if the instance is invalid
    protected void validateCreate(T newInstance) {}
    protected void validateUpdate(String id,T newInstance) {}
    protected void validateDelete(String id) {}

    protected void authorize(String action) {
        if ( ! SecurityUtils.getSubject().isPermitted(getPermissionDomain() + ":" + action) ) {
            throw new UnauthorizedException();
        }
    }

    public T create(T newInstance) {
        authorize("create");
        validateCreate(newInstance);
        return getDAO().create(newInstance);
    }

    public T update(@PathParam("id") String id, T newInstance) {
        authorize("update");
        validateUpdate(id,newInstance);
        return getDAO().update(id, newInstance);
    }

    public Response delete(@PathParam("id") String id) {
        authorize("delete");
        validateDelete(id);
        getDAO().delete(id);
        return Response.ok().build();
    }
}
