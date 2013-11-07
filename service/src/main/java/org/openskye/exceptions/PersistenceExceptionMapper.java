package org.openskye.exceptions;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class PersistenceExceptionMapper implements ExceptionMapper<PersistenceException> {
    @Override
    public Response toResponse(PersistenceException exception) {
        Response.Status status;
        ExceptionMessage em = new ExceptionMessage();
        if (exception instanceof EntityExistsException) {
            em.setErrorCode(6001);
            em.setMessage("This entity already exists");
            status = Response.Status.BAD_REQUEST;
        } else if (exception instanceof EntityNotFoundException) {
            em.setErrorCode(6002);
            em.setMessage("The entity you're looking for is not found");
            status = Response.Status.NOT_FOUND;
        } else {
            em.setErrorCode(6003);
            em.setMessage("There was a problem persisting this entity to the database");
            status = Response.Status.INTERNAL_SERVER_ERROR;
        }

        return Response.status(status).entity(em).type("application/json").build();
    }
}
