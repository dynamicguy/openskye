package org.openskye.resource;

import com.google.common.base.Optional;
import com.sun.jersey.api.client.ClientResponse;
import io.dropwizard.testing.junit.ResourceTestRule;
import io.dropwizard.util.Generics;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Before;
import org.junit.Test;
import org.openskye.domain.Domain;
import org.openskye.domain.Identifiable;
import org.openskye.domain.User;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;

/**
 * An abstract base for all the resource testing
 */
public abstract class AbstractResourceTest<T extends Identifiable> {

    private final Subject subject = mock(Subject.class);
    private Class<T> entityClass = (Class<T>) Generics.getTypeParameter(getClass());

    public abstract String getSingular();

    public abstract String getPlural();

    public abstract ResourceTestRule getResources();

    public abstract T getInstance();

    public abstract AbstractPaginatingDAO getDAO();

    public abstract PaginatedResult getExpectedResult();

    @Before
    public void setUp() {

        Domain domain = new Domain();
        domain.setName("Domain");

        User user = new User();
        user.setDomain(domain);

        when(getDAO().list()).thenReturn(getExpectedResult());
        when(getDAO().get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(Optional.of(getInstance()));
        when(getDAO().delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(getDAO().create(any(Identifiable.class))).thenReturn(getInstance());
        when(getDAO().update("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9", getInstance())).thenReturn(getInstance());
        when(subject.getPrincipal()).thenReturn(user);

    }

    @Test
    public void testAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":update")).thenReturn(true);
        assertThat(getResources().client().resource("/api/1/" + getPlural() + "/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(entityClass, getInstance()), equalTo(getInstance()));
    }

    @Test
     public void testUnAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":update")).thenReturn(false);
        assertThat(getResources().client().resource("/api/1/" + getPlural() + "/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, getInstance()).getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":create")).thenReturn(true);
        assertThat(getResources().client().resource("/api/1/" + getPlural()).type(MediaType.APPLICATION_JSON_TYPE).post(entityClass, getInstance()), equalTo(getInstance()));
    }

    @Test
    public void testUnAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":create")).thenReturn(false);
        assertThat(getResources().client().resource("/api/1/" + getPlural()).type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, getInstance()).getStatus(), equalTo(401));
    }

    @Test
     public void testAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":list")).thenReturn(true);
        assertThat(getResources().client().resource("/api/1/" + getPlural()).get(PaginatedResult.class), equalTo(getExpectedResult()));
    }

    @Test
    public void testUnAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":list")).thenReturn(false);
        assertThat(getResources().client().resource("/api/1/" + getPlural()).get(ClientResponse.class).getStatus(), equalTo(401));
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
        assertThat(getResources().client().resource("/api/1/" + getPlural() + "/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class).getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorizedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":get")).thenReturn(true);
        assertThat(getResources().client().resource("/api/1/" + getPlural() + "/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(entityClass), equalTo(getInstance()));
        verify(getDAO()).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGetMissing() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":get")).thenReturn(false);
        assertThat(getResources().client().resource("/api/1/" + getPlural() + "/60ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(ClientResponse.class).getStatus(), equalTo(401));
    }

    @Test
    public void testAuthorisedGetMissing() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":get")).thenReturn(true);
        assertThat(getResources().client().resource("/api/1/" + getPlural() + "60ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(ClientResponse.class).getStatus(), equalTo(404));
    }

    @Test
    public void testUnAuthorisedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":get")).thenReturn(false);
        assertThat(getResources().client().resource("/api/1/" + getPlural() + "/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(ClientResponse.class).getStatus(), equalTo(401));
    }
}
