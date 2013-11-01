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

            return Response.status(Response.Status.BAD_REQUEST).entity(example).type("application/json").entity("This is how that object was supposed to look").build();
        } catch (Exception e) {
            throw new SkyeException("...Really?", e);
        }

    }
}
