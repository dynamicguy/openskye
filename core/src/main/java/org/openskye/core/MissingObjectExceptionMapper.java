package org.openskye.core;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class MissingObjectExceptionMapper implements ExceptionMapper<MissingObjectException> {
    @Override
    public Response toResponse(MissingObjectException exception) {
        return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).type("application/json").build();
    }
}
