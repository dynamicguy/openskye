package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import org.skye.domain.Permission;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.PermissionDAO;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/permissions", description = "Manage permmisions")
@Path("/api/1/permissions")
@Produces(MediaType.APPLICATION_JSON)
/**
 * Manage domains
 */
public class PermissionResource extends AbstractUpdatableDomainResource<Permission> {

    @Inject
    protected PermissionDAO permissionDAO;

    @Override
    protected AbstractPaginatingDAO<Permission> getDAO() {
        return permissionDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "permission";
    }


}
