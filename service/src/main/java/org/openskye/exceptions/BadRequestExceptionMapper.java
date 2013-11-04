package org.openskye.exceptions;


import org.openskye.core.SkyeException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {
    @Override
    public Response toResponse(BadRequestException exception) {

        String throwingClass = exception.getStackTrace()[0].getClassName();
        try {
            Object example = Class.forName(throwingClass).newInstance();
            ExceptionMessage em = new ExceptionMessage(4000, "Bad Request: ", exception.getMessage() + example);
            return Response.status(Response.Status.BAD_REQUEST).entity(em).type("application/json").build();
        } catch (Exception e) {
            throw new SkyeException("Could not create example for exception", e);
        }

    }
}
