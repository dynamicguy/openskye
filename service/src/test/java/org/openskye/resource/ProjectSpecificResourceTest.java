package org.openskye.resource;

import com.sun.jersey.api.client.ClientResponse;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.openskye.domain.Identifiable;
import org.openskye.domain.dao.PaginatedResult;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;

/**
 * User: atcmostafavi
 * Date: 12/10/13
 * Time: 1:06 PM
 * Project: platform
 */
public abstract class ProjectSpecificResourceTest<T extends Identifiable> extends AbstractResourceTest<T> {


    public static final String projectID="59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9";

    @Override
    @Test
    public void testAuthorizedPut() throws Exception{
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":update:" + projectID)).thenReturn(true);
        when(subject.isPermitted(getSingular()+":get:"+projectID)).thenReturn(true);
        assertThat(getResources().client().resource("/api/1/" + getPlural() + "/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(entityClass, getInstance()), equalTo(getInstance()));
    }

    @Override
    @Test
    public void testUnAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":update:"+projectID)).thenReturn(false);
        assertThat(getResources().client().resource("/api/1/" + getPlural() + "/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, getInstance()).getStatus(), equalTo(401));
    }

    @Override
    @Test
    public void testAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":create:"+projectID)).thenReturn(true);
        when(subject.isPermitted(getSingular() + ":create:")).thenReturn(true);
        assertThat(getResources().client().resource("/api/1/" + getPlural()).type(MediaType.APPLICATION_JSON_TYPE).post(entityClass, getInstance()), equalTo(getInstance()));
    }

    @Override
    @Test
    public void testUnAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":create:"+projectID)).thenReturn(false);
        when(subject.isPermitted(getSingular() + ":create:")).thenReturn(true);
        assertThat(getResources().client().resource("/api/1/" + getPlural()).type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, getInstance()).getStatus(), equalTo(401));
    }

    @Override
    @Test
    public void testAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":list:"+projectID)).thenReturn(true);
        when(subject.isPermitted(getSingular() + ":list:")).thenReturn(true);
        assertThat(getResources().client().resource("/api/1/" + getPlural()).get(PaginatedResult.class), equalTo(getExpectedResult()));
    }

    @Override
    @Test
    public void testUnAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":list:"+projectID)).thenReturn(false);
        when(subject.isPermitted(getSingular() + ":list:")).thenReturn(false);
        assertThat(getResources().client().resource("/api/1/" + getPlural()).get(ClientResponse.class).getStatus(), equalTo(401));
    }

    @Override
    @Test
    public void testAuthorizedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":delete:"+projectID)).thenReturn(true);
        assertThat(getResources().client().resource("/api/1/" + getPlural() + "/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class).getStatus(), equalTo(200));
        verify(getDAO()).delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Override
    @Test
    public void testUnAuthorisedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":delete:"+projectID)).thenReturn(false);
        assertThat(getResources().client().resource("/api/1/" + getPlural() + "/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class).getStatus(), equalTo(401));
    }

    @Override
    @Test
    public void testAuthorizedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":get:"+projectID)).thenReturn(true);
        when(subject.isPermitted(getSingular()+":get:")).thenReturn(true);
        assertThat(getResources().client().resource("/api/1/" + getPlural() + "/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(entityClass), equalTo(getInstance()));
        verify(getDAO(),atLeastOnce()).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Override
    @Test
    public void testUnAuthorisedGetMissing() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":get:60ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(false);
        when(subject.isPermitted(getSingular() + ":get: ")).thenReturn(false);
        assertThat(getResources().client().resource("/api/1/" + getPlural() + "/60ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(ClientResponse.class).getStatus(), equalTo(401));
    }

    @Override
    @Test
    public void testAuthorisedGetMissing() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":get:60ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(subject.isPermitted(getSingular() + ":get: ")).thenReturn(true);
        assertThat(getResources().client().resource("/api/1/" + getPlural() + "60ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(ClientResponse.class).getStatus(), equalTo(404));
    }

    @Override
    @Test
    public void testUnAuthorisedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular() + ":get:"+projectID)).thenReturn(false);
        assertThat(getResources().client().resource("/api/1/" + getPlural() + "/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(ClientResponse.class).getStatus(), equalTo(401));
    }
}
