package org.openskye.exceptions;

import javax.persistence.EntityExistsException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EntityExistsExceptionMapper implements ExceptionMapper<EntityExistsException> {
    @Override
    public Response toResponse(EntityExistsException exception) {
        ExceptionMessage em = new ExceptionMessage(18000, "Entity exists", exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(em).type("application/json").build();
    }
}
