package org.openskye.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Unauthorized exception
 */
public class NotFoundException extends WebApplicationException {

    /**
     * Create a HTTP 404 (Unauthorized) exception.
     */
    public NotFoundException() {
        super(Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * Create a HTTP 404 (Not Found) exception.
     *
     * @param message the String that is the resources of the 404 response.
     */
    public NotFoundException(String message) {
        super(Response.status(Response.Status.NOT_FOUND).entity(message).type("text/plain").build());
    }

}
