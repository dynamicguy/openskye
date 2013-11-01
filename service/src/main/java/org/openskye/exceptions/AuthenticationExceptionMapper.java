package org.openskye.exceptions;

import org.apache.shiro.authc.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {
    @Override
    public Response toResponse(AuthenticationException exception) {
        ExceptionMessage em = new ExceptionMessage();
        if (exception instanceof AccountException) {
            em.setErrorCode(2000);
            em.setMessage("There seems to be a problem with your account.");
        } else if (exception instanceof ConcurrentAccessException) {
            em.setErrorCode(2001);
            em.setMessage("This account has already been authenticated or the user is already logged in");
        } else if (exception instanceof CredentialsException) {
            em.setErrorCode(2002);
            em.setMessage("There was a problem with your credentials");
        } else if (exception instanceof DisabledAccountException) {
            em.setErrorCode(2003);
            em.setMessage("This account has been disabled. Please see your administrator");
        } else if (exception instanceof ExcessiveAttemptsException) {
            em.setErrorCode(2004);
            em.setMessage("You have attempted to login too many times");
        } else if (exception instanceof ExpiredCredentialsException) {
            em.setErrorCode(2005);
            em.setMessage("Your login credentials have expired. Please see your administrator");
        } else if (exception instanceof IncorrectCredentialsException) {
            em.setErrorCode(2006);
            em.setMessage("You have entered incorrect login credentials. Please try again");
        } else if (exception instanceof LockedAccountException) {
            em.setErrorCode(2006);
            em.setMessage("Your account has been locked. Please see your administrator");
        } else if (exception instanceof UnknownAccountException) {
            em.setErrorCode(2007);
            em.setMessage("This account doesn't exist. Please see your administrator about creating an account for you");
        } else {
            em.setErrorCode(2009);
            em.setMessage(em.getMessage());
        }
        return Response.status(401).
                entity(em).
                type("application/json").
                build();
    }
}
