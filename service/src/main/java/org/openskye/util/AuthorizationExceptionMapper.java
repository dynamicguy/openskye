package org.openskye.util;

import org.apache.shiro.authz.AuthorizationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthorizationExceptionMapper implements
        ExceptionMapper<AuthorizationException> {
    public Response toResponse(AuthorizationException ex) {
        return Response.status(401).
                type("application/json").
                build();
    }
}
