package org.skye.resource;

import com.yammer.dropwizard.hibernate.UnitOfWork;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * An abstract base for a resource that will present
 * domain objects persisted in hibernate
 */
@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractUpdatableDomainResource<T> extends AbstractRealOnlyDomainResource<T> {

    @POST
    @UnitOfWork
    @Timed
    public T create(T newInstance) {
        return getDAO().persist(newInstance);
    }

    @Path("/{id}")
    @PUT
    @UnitOfWork
    @Timed
    public T update(@PathParam("id") String id, T newInstance) {
        // TODO need to do the merge here?
        return getDAO().persist(newInstance);
    }

    @Path("/{id}")
    @DELETE
    @UnitOfWork
    @Timed
    public Response delete(@PathParam("id") String id) {
        getDAO().delete(id);
        return Response.ok().build();
    }
}
