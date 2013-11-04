package org.openskye.exceptions;

import org.openskye.core.SkyeException;

import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {
    @Override
    public Response toResponse(ValidationException exception) {
        String throwingClass = exception.getStackTrace()[0].getClassName();
        try {
            Object example = Class.forName(throwingClass).newInstance();
            ExceptionMessage em = new ExceptionMessage(7000, "Validation error: ", exception.getMessage()+example);
            return Response.status(Response.Status.BAD_REQUEST).entity(em).type("application/json").build();
        } catch (Exception e) {
            throw new SkyeException("Could not create example for exception", e);
        }

    }
}
