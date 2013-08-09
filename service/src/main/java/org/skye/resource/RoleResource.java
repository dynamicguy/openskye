package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import org.skye.domain.Domain;
import org.skye.domain.Role;
import org.skye.resource.AbstractUpdatableDomainResource;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.RoleDAO;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/roles", description = "Manage roles")
@Path("/api/1/roles")
@Produces(MediaType.APPLICATION_JSON)
/**
 * Manage domains
 */
public class RoleResource extends AbstractUpdatableDomainResource<Role> {

    @Inject
    protected RoleDAO roleDAO;

    @Override
    protected AbstractPaginatingDAO<Role> getDAO() {
        return roleDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "role";
    }


}
