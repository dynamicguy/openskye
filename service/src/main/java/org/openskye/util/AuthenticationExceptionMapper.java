package org.openskye.util;

import org.apache.shiro.authc.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {
    @Override
    public Response toResponse(AuthenticationException exception) {
        String message;
        if(exception instanceof AccountException){
            message = "There seems to be a problem with your account.";
        }
        else if(exception instanceof ConcurrentAccessException){
            message = "This account has already been authenticated or the user is already logged in";
        }
        else if(exception instanceof CredentialsException){
            message = "There was a problem with your credentials";
        }
        else if(exception instanceof DisabledAccountException){
            message = "This account has been disabled. Please see your administrator";
        }
        else if(exception instanceof ExcessiveAttemptsException){
             message = "You have attempted to login too many times";
        }
        else if(exception instanceof ExpiredCredentialsException){
             message = "Your login credentials have expired. Please see your administrator";
        }
        else if(exception instanceof IncorrectCredentialsException){
            message = "You have entered incorrect login credentials. Please try again";
        }
        else if(exception instanceof LockedAccountException){
            message = "Your account has been locked. Please see your administrator";
        }
        else if(exception instanceof UnknownAccountException){
            message = "This account doesn't exist. Please see your administrator about creating an account for you";
        }
        else{
            message= exception.getMessage();
        }
        return Response.status(401).
                entity(message).
                type("application/json").
                build();
    }
}
