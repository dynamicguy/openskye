package org.openskye.exceptions;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        ExceptionMessage em = new ExceptionMessage(23000, "Constraint violation", exception.getMessage());
        for(ConstraintViolation violation : exception.getConstraintViolations()) {
            ExceptionMessageDetail detail = new ExceptionMessageDetail();
            detail.setPath(violation.getPropertyPath().toString());
            detail.setMessage(violation.getMessage());
            em.getDetails().add(detail);
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(em).type("application/json").build();
    }
}
