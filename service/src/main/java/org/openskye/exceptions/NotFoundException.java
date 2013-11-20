package org.openskye.exceptions;

import lombok.Getter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Not found exception
 */
public class NotFoundException extends WebApplicationException {
    @Getter
    private String entityMessage = null;

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
        super(Response.status(Response.Status.NOT_FOUND).type("text/plain").build());
        this.entityMessage = message;
    }

}
