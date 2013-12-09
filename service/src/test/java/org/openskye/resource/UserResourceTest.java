package org.openskye.resource;

import com.google.common.base.Optional;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.apache.shiro.util.ThreadContext;
import org.junit.ClassRule;
import org.junit.Test;
import org.openskye.domain.User;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.UserDAO;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserResourceTest extends AbstractResourceTest<User> {

    public static final UserDAO dao = mock(UserDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new UserResource(dao))
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
            .build();
    private User user;
    private PaginatedResult<User> expectedResult = new PaginatedResult<>();

    public UserResourceTest() {
        user = new User();
        user.setName("Test");
        user.setEmail("test@test.org");

        when(dao.findByEmail("test@test.org")).thenReturn(Optional.of(user));
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