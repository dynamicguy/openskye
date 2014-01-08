package org.openskye.cli;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * An implementation of the Java exception mapper for the Skye command line
 */
@Provider
public class CliExceptionMapper implements ExceptionMapper<CliException> {

    /**
     * Maps a command line exception to a Response
     *
     * @param exception the CliException thrown
     *
     * @return a Response with a 500 HTML status
     */
    @Override
    public Response toResponse(CliException exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).type("application/json").build();
    }
}
