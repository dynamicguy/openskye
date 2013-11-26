package org.openskye.exceptions;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class EntityNotFoundExceptionMapper implements ExceptionMapper<EntityNotFoundException> {
    @Override
    public Response toResponse(EntityNotFoundException exception) {
        ExceptionMessage em = new ExceptionMessage(22000, "Entity not found", exception.getMessage());
        return Response.status(Response.Status.NOT_FOUND).entity(em).type("application/json").build();
    }
}
