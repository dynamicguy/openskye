package org.skye.resource;

import com.yammer.dropwizard.hibernate.UnitOfWork;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.util.PaginatedResult;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * An abstract base for a resource that will present
 * domain objects persisted in hibernate
 */
@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractDomainResource<T> {

    private AbstractPaginatingDAO<T> dao;

    public AbstractDomainResource(AbstractPaginatingDAO<T> dao) {
        this.dao = dao;
    }

    @Path("/")
    @GET
    @UnitOfWork
    public PaginatedResult<T> getAll() {
        return dao.list();
    }

}
