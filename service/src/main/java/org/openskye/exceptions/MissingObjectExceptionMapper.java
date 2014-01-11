package org.openskye.exceptions;

import org.openskye.core.MissingObjectException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class MissingObjectExceptionMapper implements ExceptionMapper<MissingObjectException> {
    @Override
    public Response toResponse(MissingObjectException exception) {
        return Response.status(Response.Status.NOT_FOUND).entity("The object you are trying to reach is missing!").type("application/json").build();
    }
}
