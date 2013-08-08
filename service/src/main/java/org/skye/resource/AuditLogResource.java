package org.skye.resource;

import org.skye.domain.AuditLog;
import org.skye.domain.Domain;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.AuditLogDAO;
import org.skye.resource.dao.DomainDAO;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
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


}
