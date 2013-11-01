package org.openskye.util;

import javax.persistence.EntityExistsException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EntityExistsExceptionMapper implements ExceptionMapper<EntityExistsException> {
    @Override
    public Response toResponse(EntityExistsException exception) {

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
