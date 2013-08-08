package org.skye.resource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Unauthorized exception
 */
public class UnauthorizedException extends WebApplicationException {

    /**
     * Create a HTTP 401 (Unauthorized) exception.
     */
    public UnauthorizedException() {
        super(Response.status(Response.Status.UNAUTHORIZED).build());
    }

    /**
     * Create a HTTP 404 (Not Found) exception.
     *
     * @param message the String that is the entity of the 404 response.
     */
    public UnauthorizedException(String message) {
        super(Response.status(Response.Status.UNAUTHORIZED).entity(message).type("text/plain").build());
    }

}
