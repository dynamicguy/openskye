package org.openskye.resource;

import com.google.common.base.Optional;
import org.apache.shiro.SecurityUtils;
import org.openskye.domain.Identifiable;
import org.openskye.domain.User;
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

    protected void authorize(String action) {
        if (!SecurityUtils.getSubject().isPermitted(getPermissionDomain() + ":" + action)) {
            throw new UnauthorizedException();
        }
    }

    public PaginatedResult<T> getAll() {
        authorize("list");
        return getDAO().list();
    }

    public T get(@PathParam("id") String id) {
        authorize("get");
        Optional<T> result = getDAO().get(id);
        if (result.isPresent())
            return result.get();
        else
            throw new NotFoundException();
    }

    protected User getCurrentUser() {
        return (User) SecurityUtils.getSubject().getPrincipal();
    }


}
