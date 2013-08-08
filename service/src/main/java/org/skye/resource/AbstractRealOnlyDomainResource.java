package org.skye.resource;

import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import com.yammer.metrics.annotation.Timed;
import org.apache.shiro.SecurityUtils;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.util.PaginatedResult;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * An abstract base for a resource that will present
 * domain objects persisted in hibernate
 */
@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractRealOnlyDomainResource<T> {

    protected abstract AbstractPaginatingDAO<T> getDAO();

    protected abstract String getPermissionDomain();

    @ApiOperation(value = "List all", notes = "Returns all results in a paginated structure")
    @GET
    @UnitOfWork
    @Timed
    public PaginatedResult<T> getAll() {
        if (SecurityUtils.getSubject().isPermitted(getPermissionDomain() + ":list")) {
            return getDAO().list();
        } else {
            throw new UnauthorizedException();
        }
    }

    @ApiOperation(value = "Find by id", notes = "Return an instance by id")
    @Path("/{id}")
    @GET
    @UnitOfWork
    @Timed
    public T get(@PathParam("id") String id) {
        // TODO need to do the merge here?
        if (SecurityUtils.getSubject().isPermitted(getPermissionDomain() + ":get")) {
            return getDAO().get(id);
        } else {
            throw new UnauthorizedException();
        }

    }

}
