package org.openskye.exceptions;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        ExceptionMessage em = new ExceptionMessage(23000, "Constrain violation", exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(em).type("application/json").build();
    }
}
