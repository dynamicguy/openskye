package org.openskye.resource;

import com.google.common.base.Optional;
import org.apache.shiro.SecurityUtils;
import org.openskye.domain.Identifiable;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.util.NotFoundException;
import org.openskye.util.UnauthorizedException;

import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * An abstract base for a resource that will present
 * domain objects persisted in hibernate
 */
@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractRealOnlyDomainResource<T extends Identifiable> {
    protected abstract AbstractPaginatingDAO<T> getDAO();

    protected abstract String getPermissionDomain();

    public PaginatedResult<T> getAll() {
        if (SecurityUtils.getSubject().isPermitted(getPermissionDomain() + ":list")) {
            return getDAO().list();
        } else {
            throw new UnauthorizedException();
        }
    }

    public T get(@PathParam("id") String id) {
        // TODO need to do the merge here?
        if (SecurityUtils.getSubject().isPermitted(getPermissionDomain() + ":get")) {
            Optional<T> result = getDAO().get(id);
            if (result.isPresent())
                return result.get();
            else
                throw new NotFoundException();
        } else {
            throw new UnauthorizedException();
        }

    }

}
