package org.skye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.skye.domain.User;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.PaginatedResult;
import org.skye.domain.dao.UserDAO;

import static org.mockito.Mockito.mock;

public class UserResourceTest extends AbstractResourceTest<User> {

    public static final UserDAO dao = mock(UserDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new UserResource(dao))
            .build();
    private final User user = new User();
    private PaginatedResult<User> expectedResult = new PaginatedResult<>();

    @Override
    public String getSingular() {
        return "user";
    }

    @Override
    public String getPlural() {
        return "users";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public User getInstance() {
        return user;
    }

    @Override
    public AbstractPaginatingDAO getDAO() {
        return dao;
    }

    @Override
    public PaginatedResult getExpectedResult() {
        return expectedResult;
    }
}