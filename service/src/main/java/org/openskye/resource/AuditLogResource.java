package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.domain.AuditLog;
import org.openskye.domain.AuditLogProperty;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.AuditLogDAO;
import org.openskye.domain.dao.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link AuditLog}
 */
@Api(value = "/api/1/auditLogs", description = "Manage audit logs")
@Path("/api/1/auditLogs")
@Produces(MediaType.APPLICATION_JSON)
public class AuditLogResource extends AbstractUpdatableDomainResource<AuditLog> {

    private AuditLogDAO auditLogDAO;

    @Inject
    public AuditLogResource(AuditLogDAO dao) {
        this.auditLogDAO = dao;
    }

    @ApiOperation(value = "Create new audit log", notes = "Create a new audit log and return with its unique id", response = AuditLog.class)
    @POST
    @Transactional
    @Timed
    public AuditLog create(AuditLog newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update audit log", notes = "Update the audit log by its id", response = AuditLog.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public AuditLog update(@PathParam("id") String id, AuditLog newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find audit log by id", notes = "Return an audit log by its unique id", response = AuditLog.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public AuditLog get(@PathParam("id") String id) {
        return super.get(id);
    }

    @ApiOperation(value = "List all audit logs", notes = "Returns all audit logs in a paginated structure", responseContainer = "List", response = AuditLogProperty.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<AuditLog> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete audit log", notes = "Deletes the audit log (found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
    }

    @Override
    protected AbstractPaginatingDAO<AuditLog> getDAO() {
        return auditLogDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "auditLog";
    }

    @Path("/{id}/auditLogProperties")
    @GET
    @ApiOperation(value = "Return the properties for this audit log")
    public PaginatedResult<AuditLogProperty> getAuditLogProperties(@PathParam("id") String id) {
        AuditLog auditLog = get(id);
        return new PaginatedResult<AuditLogProperty>().paginate(auditLog.getAuditLogProperties());

    }

    @Path("/user/{user}")
    @GET
    @ApiOperation(value = "Return the audit log entries associated with the user. Param can be id or email")
    public PaginatedResult<AuditLog> getUserActivities(@PathParam("user") String user) {
        PaginatedResult<AuditLog> logs = new PaginatedResult<>();
        if(auditLogDAO.findByUser(user).isPresent()){
            logs= logs.paginate(auditLogDAO.findByUser(user).get());
        }
        return logs;
    }

    @Path("/object/{object}")
    @GET
    @ApiOperation(value = "Return the audit log entries associated with the user. Param can be id or email")
    public PaginatedResult<AuditLog> getObjectEvents(@PathParam("object") String object) {
        PaginatedResult<AuditLog> logs = new PaginatedResult<>();
        if(auditLogDAO.findByObject(object).isPresent()){
            logs= logs.paginate(auditLogDAO.findByObject(object).get());
        }
        return logs;
    }




}
