package org.openskye.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptonMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException exception) {
        String whatWasMissing = exception.getStackTrace()[0].getClassName();
        String message = "The " + whatWasMissing + " you were looking for was not found";
        return Response.status(Response.Status.NOT_FOUND).entity(message).type("application/json").build();
    }
}
