package org.skye.resource;

import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.skye.domain.AuditLog;
import org.skye.domain.AuditLogProperty;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.AuditLogDAO;
import org.skye.util.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/auditLogs", description = "Manage audit logs")
@Path("/api/1/auditLogs")
@Produces(MediaType.APPLICATION_JSON)
/**
 * Manage domains
 */
public class AuditLogResource extends AbstractUpdatableDomainResource<AuditLog> {

    @Inject
    protected AuditLogDAO auditLogDAO;

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


}
