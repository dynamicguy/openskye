package org.openskye.cli;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class CliExceptionMapper implements ExceptionMapper<CliException> {
    @Override
    public Response toResponse(CliException exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).type("application/json").build();
    }
}
