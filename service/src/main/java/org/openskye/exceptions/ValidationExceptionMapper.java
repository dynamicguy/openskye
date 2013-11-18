package org.openskye.exceptions;

import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {
    @Override
    public Response toResponse(ValidationException exception) {
        ExceptionMessage em = new ExceptionMessage(7000, "Validation error: ", exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(em).type("application/json").build();
    }
}
