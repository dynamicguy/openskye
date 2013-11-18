package org.openskye.exceptions;


import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {
    @Override
    public Response toResponse(BadRequestException exception) {
        ExceptionMessage em = new ExceptionMessage(4000, "Bad request", exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(em).type("application/json").build();
    }
}
