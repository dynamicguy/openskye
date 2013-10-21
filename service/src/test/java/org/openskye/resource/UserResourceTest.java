package org.openskye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.openskye.domain.User;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.UserDAO;

import static org.mockito.Mockito.mock;

public class UserResourceTest extends AbstractResourceTest<User> {

    public static final UserDAO dao = mock(UserDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new UserResource(dao))
            .build();
    private User user;
    private PaginatedResult<User> expectedResult = new PaginatedResult<>();

    public UserResourceTest() {
        user = new User();
        user.setName("Test");
        user.setEmail("test@test.org");
    }

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