package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import org.skye.domain.User;
import org.skye.resource.dao.AbstractPaginatingDAO;

import javax.ws.rs.Path;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/users", description = "Manage users")
@Path("/api/1/users")
public class UserResource extends AbstractUpdatableDomainResource<User> {

    public UserResource(AbstractPaginatingDAO<User> dao) {
        super(dao);
    }
}
