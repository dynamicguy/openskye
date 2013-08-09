package org.skye.resource;

import org.skye.domain.AuditLogProperty;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.AuditLogPropertyDAO;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Path("/api/1/auditLogProperties")
@Produces(MediaType.APPLICATION_JSON)
/**
 * Manage domains
 */
public class AuditLogPropertyResource extends AbstractUpdatableDomainResource<AuditLogProperty> {

    @Inject
    protected AuditLogPropertyDAO auditLogPropertyDAO;

    @Override
    protected AbstractPaginatingDAO<AuditLogProperty> getDAO() {
        return auditLogPropertyDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "auditLogProperty";
    }


}
