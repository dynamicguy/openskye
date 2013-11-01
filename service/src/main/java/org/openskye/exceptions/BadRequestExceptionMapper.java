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
                return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).entity(example).type("application/json").build();
            } catch (Exception e) {
                throw new SkyeException("...Really?", e);
            }

    }
}
