package org.openskye.exceptions;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Created with IntelliJ IDEA.
 * User: atcmostafavi
 * Date: 11/1/13
 * Time: 2:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class PersistenceExceptionMapper implements ExceptionMapper<PersistenceException> {
    @Override
    public Response toResponse(PersistenceException exception) {
        String message;
        Response.Status status;
        if(exception instanceof EntityExistsException){
            message = "This entity already exists";
            status = Response.Status.BAD_REQUEST;
        }
        else if(exception instanceof EntityNotFoundException){
            message = "The entity you're looking for is not found";
            status = Response.Status.NOT_FOUND;
        }
        else{
            message = "There was a problem persisting this entity to the database";
            status = Response.Status.INTERNAL_SERVER_ERROR;
        }

        return Response.status(status).entity(message).type("application/json").build();
    }
}
