package org.openskye.core;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SkyeExceptionMapper implements ExceptionMapper<SkyeException> {

    @Override
    public Response toResponse(SkyeException exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).type("application/json").build();
    }
}
