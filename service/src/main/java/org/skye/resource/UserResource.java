package org.skye.resource;

import org.skye.domain.User;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.UserDAO;

import javax.inject.Inject;
import javax.ws.rs.Path;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Path("/api/1/users")
public class UserResource extends AbstractUpdatableDomainResource<User> {

    @Inject
    private UserDAO userDAO;

    @Override
    protected AbstractPaginatingDAO<User> getDAO() {
        return userDAO;
    }
}
