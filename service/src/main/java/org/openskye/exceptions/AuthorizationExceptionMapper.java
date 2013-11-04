package org.openskye.exceptions;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.HostUnauthorizedException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthorizationExceptionMapper implements
        ExceptionMapper<AuthorizationException> {
    public Response toResponse(AuthorizationException ex) {
        ExceptionMessage message = new ExceptionMessage();
        if (ex instanceof HostUnauthorizedException) {
            message.setErrorCode(3000);
            message.setMessage("Your host address has not been authorized to access this area");
        } else if (ex instanceof UnauthenticatedException) {
            message.setErrorCode(3001);
            message.setMessage("You have not been authenticated and cannot perform this operation. Please login first");
        } else if (ex instanceof UnauthorizedException) {
            message.setErrorCode(3002);
            message.setMessage("You are not authorized to perform this operation");
        } else {
            message.setErrorCode(3003);
            message.setMessage(ex.getMessage());
        }
        return Response.status(401).
                entity(message).
                type("application/json").
                build();
    }
}
