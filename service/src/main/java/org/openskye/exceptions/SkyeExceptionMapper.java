package org.openskye.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.openskye.core.SkyeException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Slf4j
@Provider
public class SkyeExceptionMapper implements
        ExceptionMapper<SkyeException> {
    public Response toResponse(SkyeException ex) {
        log.error("Skye Error",ex);
        ExceptionMessage message = new ExceptionMessage();
        message.setErrorCode(5000);
        message.setMessage(ex.getMessage());
        return Response.status(500).
                entity(message).
                type("application/json").
                build();
    }
}
