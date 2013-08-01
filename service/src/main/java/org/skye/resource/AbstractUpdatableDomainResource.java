package org.skye.resource;

import com.yammer.dropwizard.hibernate.UnitOfWork;
import org.skye.resource.dao.AbstractPaginatingDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * An abstract base for a resource that will present
 * domain objects persisted in hibernate
 */
@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractUpdatableDomainResource<T> extends AbstractRealOnlyDomainResource<T> {

    private AbstractPaginatingDAO<T> dao;

    public AbstractUpdatableDomainResource(AbstractPaginatingDAO<T> dao) {
        super(dao);
        this.dao = dao;
    }

    @Path("/")
    @POST
    @UnitOfWork
    public T create(T newInstance) {
        return dao.persist(newInstance);
    }

    @Path("/{id}")
    @PUT
    @UnitOfWork
    public T update(@PathParam("id") String id, T newInstance) {
        // TODO need to do the merge here?
        return dao.persist(newInstance);
    }

    @Path("/{id}")
    @DELETE
    @UnitOfWork
    public Response delete(@PathParam("id") String id) {
        dao.delete(id);
        return Response.ok().build();
    }


}
