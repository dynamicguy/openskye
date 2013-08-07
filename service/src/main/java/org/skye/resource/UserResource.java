package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import org.skye.domain.User;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.UserDAO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.Path;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/users", description = "Manage users")
@Path("/api/1/users")
@Service
public class UserResource extends AbstractUpdatableDomainResource<User> {

    @Inject
    private UserDAO userDAO;

    @Override
    protected AbstractPaginatingDAO<User> getDAO() {
        return userDAO;
    }
}
