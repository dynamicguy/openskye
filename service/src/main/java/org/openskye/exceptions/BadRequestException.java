package org.openskye.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Web exception for missing resources
 */
public class BadRequestException extends WebApplicationException {
    @Getter
    private String entityMessage = null;

    /**
     * Create a HTTP 404 (Unauthorized) exception.
     */
    public BadRequestException() {
        super(Response.status(Response.Status.BAD_REQUEST).build());
    }

    /**
     * Create a HTTP 404 (Not Found) exception.
     *
     * @param message the String that is the resources of the 404 response.
     */
    public BadRequestException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST).type("text/plain").build());
        this.entityMessage = message;
    }

}
