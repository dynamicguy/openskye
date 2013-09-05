package org.skye.resource;

import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.skye.domain.AuditLogProperty;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.AuditLogPropertyDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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

    @Inject
    protected AuditLogPropertyDAO auditLogPropertyDAO;

    @ApiOperation(value = "Create new audit log property", notes = "Create a new audit log property and return with its unique id", response = AuditLogProperty.class)
    @POST
    @Transactional
    @Timed
    public AuditLogProperty create(AuditLogProperty newInstance){
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

    @Override
    protected AbstractPaginatingDAO<AuditLogProperty> getDAO() {
        return auditLogPropertyDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "auditLogProperty";
    }


}
