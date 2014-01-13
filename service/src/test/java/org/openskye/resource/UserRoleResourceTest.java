package org.openskye.resource;


import com.google.common.base.Optional;
import com.sun.jersey.api.client.ClientResponse;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.openskye.domain.Domain;
import org.openskye.domain.User;
import org.openskye.domain.UserRole;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.UserRoleDAO;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class UserRoleResourceTest {
    private final Subject subject = mock(Subject.class);

    @Before
    public void setUp() {

        Domain domain = new Domain();
        domain.setName("Domain");

        User user = new User();
        user.setDomain(domain);

        when(getDAO().list()).thenReturn(getExpectedResult());
        when(getDAO().get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(Optional.of(getInstance()));
        when(getDAO().delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(getDAO().create(any(UserRole.class))).thenReturn(getInstance());
        when(getDAO().update("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9", getInstance())).thenReturn(getInstance());
        when(subject.getPrincipal()).thenReturn(user);

    }

    public static final UserRoleDAO dao = mock(UserRoleDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new UserRoleResource(dao))
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
            .build();

    private final UserRole userRole=new UserRole();
    private PaginatedResult<UserRole> expectedResult=new PaginatedResult<>();


    public String getSingular() {
        return "userRole";
    }


    public String getPlural() {
        return "userRoles";
    }


    public ResourceTestRule getResources() {
        return resources;
    }


    public UserRole getInstance() {
        return userRole;
    }


    public AbstractPaginatingDAO getDAO() {
        return dao;
    }


    public PaginatedResult getExpectedResult() {
        return expectedResult;
    }
    @Test
    public void testUnAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":list")).thenReturn(false);
        assertThat(getResources().client().resource("/api/1/" + getPlural()).get(ClientResponse.class).getStatus(), equalTo(403));
    }
    @Test
    public void testAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":list")).thenReturn(true);
        assertThat(getResources().client().resource("/api/1/" + getPlural()).get(PaginatedResult.class), equalTo(getExpectedResult()));
    }
            @Test
    public void testUnAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":create")).thenReturn(false);
        assertThat(getResources().client().resource("/api/1/" + getPlural()).type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, getInstance()).getStatus(), equalTo(403));
    }
     @Test
        public void testAuthorizedPost() throws Exception {
            ThreadContext.bind(subject);
            when(subject.isPermitted(getSingular() + ":create")).thenReturn(true);
            assertThat(getResources().client().resource("/api/1/" + getPlural()).type(MediaType.APPLICATION_JSON_TYPE).post(UserRole.class, getInstance()), equalTo(getInstance()));

}
    @Test
    public void testAuthorizedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":delete")).thenReturn(true);
        assertThat(getResources().client().resource("/api/1/" + getPlural() + "/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class).getStatus(), equalTo(200));
        verify(getDAO()).delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }
    @Test
    public void testUnAuthorisedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":delete")).thenReturn(false);
        assertThat(getResources().client().resource("/api/1/" + getPlural() + "/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class).getStatus(), equalTo(403));
    }
}
