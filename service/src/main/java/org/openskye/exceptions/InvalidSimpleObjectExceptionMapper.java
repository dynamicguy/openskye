package org.openskye.exceptions;

import org.openskye.core.InvalidSimpleObjectException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidSimpleObjectExceptionMapper implements ExceptionMapper<InvalidSimpleObjectException> {
    @Override
    public Response toResponse(InvalidSimpleObjectException exception) {
        ExceptionMessage em = new ExceptionMessage(1000,"Invalid simple object",exception.getMessage()+exception.getSimpleObjectClass());
        return Response.status(Response.Status.BAD_REQUEST).entity(em).type("application/json").build();
    }
}
