package org.skye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.skye.domain.AuditLogProperty;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.AuditLogPropertyDAO;
import org.skye.domain.dao.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/auditLogProperties", description = "Manage audit log properties")
@Path("/api/1/auditLogProperties")
@Produces(MediaType.APPLICATION_JSON)
/**
 * Manage domains
 */
public class AuditLogPropertyResource extends AbstractUpdatableDomainResource<AuditLogProperty> {


    protected AuditLogPropertyDAO auditLogPropertyDAO;

    @Inject
    public AuditLogPropertyResource(AuditLogPropertyDAO dao) {
        auditLogPropertyDAO = dao;
    }

    @ApiOperation(value = "Create new audit log property", notes = "Create a new audit log property and return with its unique id", response = AuditLogProperty.class)
    @POST
    @Transactional
    @Timed
    public AuditLogProperty create(AuditLogProperty newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update audit log property", notes = "Enter the id of the audit log property to update and its new information. Returns updated property", response = AuditLogProperty.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public AuditLogProperty update(@PathParam("id") String id, AuditLogProperty newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find audit log property by id", notes = "Return an audit log property by id", response = AuditLogProperty.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public AuditLogProperty get(@PathParam("id") String id) {
        return super.get(id);
    }

    @ApiOperation(value = "List all audit log properties", notes = "Returns all audit log properties in a paginated structure", responseContainer = "List", response = AuditLogProperty.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<AuditLogProperty> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete audit log property", notes = "Deletes the audit log property(found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
    }

    @Override
    protected AbstractPaginatingDAO<AuditLogProperty> getDAO() {
        return auditLogPropertyDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "auditLogProperty";
    }


}
