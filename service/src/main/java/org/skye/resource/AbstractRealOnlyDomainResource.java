package org.skye.resource;

import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import com.yammer.metrics.annotation.Timed;
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

    @ApiOperation(value = "Return all", notes = "Returns a list of all")
    @Path("/")
    @GET
    @UnitOfWork
    @Timed
    public PaginatedResult<T> getAll() {
        return getDAO().list();
    }

    @ApiOperation(value = "Return one identified by identifier", notes = "Returns a single instance")
    @Path("/{id}")
    @GET
    @UnitOfWork
    @Timed
    public T get(@PathParam("id") String id) {
        // TODO need to do the merge here?
        return getDAO().get(id);
    }

}
