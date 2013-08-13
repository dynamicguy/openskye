package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.skye.domain.AuditLog;
import org.skye.domain.AuditLogProperty;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.AuditLogDAO;
import org.skye.util.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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

    @Override
    protected AbstractPaginatingDAO<AuditLog> getDAO() {
        return auditLogDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "auditLog";
    }

    @Path("/{id}/archiveStores")
    @GET
    @ApiOperation(value = "Return the properties for this audit log")
    public PaginatedResult<AuditLogProperty> getAuditLogProperties(@PathParam("id") String id) {
        AuditLog auditLog = get(id);
        return new PaginatedResult<AuditLogProperty>().paginate(auditLog.getAuditLogProperties());

    }


}
