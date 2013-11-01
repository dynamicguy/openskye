package org.openskye.core;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidSimpleObjectExceptionMapper implements ExceptionMapper<InvalidSimpleObjectException> {
    @Override
    public Response toResponse(InvalidSimpleObjectException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()+exception.getSimpleObjectClass()).type("application/json").build();
    }
}
