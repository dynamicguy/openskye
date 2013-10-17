package org.openskye.util;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Web exception for missing entity
 */
public class BadRequestException extends WebApplicationException {

    /**
     * Create a HTTP 404 (Unauthorized) exception.
     */
    public BadRequestException() {
        super(Response.status(Response.Status.BAD_REQUEST).build());
    }

    /**
     * Create a HTTP 404 (Not Found) exception.
     *
     * @param message the String that is the entity of the 404 response.
     */
    public BadRequestException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST).entity(message).type("text/plain").build());
    }

}
