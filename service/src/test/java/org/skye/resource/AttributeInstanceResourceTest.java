package org.skye.resource;

import com.google.common.base.Optional;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.AttributeInstance;
import org.skye.domain.dao.AttributeInstanceDAO;
import org.skye.domain.dao.PaginatedResult;

import javax.ws.rs.core.MediaType;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AttributeInstanceResourceTest extends ResourceTest {
    private final AttributeInstance attributeInstance = new AttributeInstance();
    private final AttributeInstanceDAO dao = mock(AttributeInstanceDAO.class);
    private final Subject subject = mock(Subject.class);
    private PaginatedResult<AttributeInstance> expectedResult = new PaginatedResult<>();

    @Override
    protected void setUpResources() {
        when(dao.list()).thenReturn(expectedResult);
        when(dao.delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(Optional.of(attributeInstance));
        when(dao.create(attributeInstance)).thenReturn(attributeInstance);
        when(dao.update("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9", attributeInstance)).thenReturn(attributeInstance);

        AttributeInstanceResource attributeInstanceResource = new AttributeInstanceResource();
        attributeInstanceResource.attributeInstanceDAO = dao;
        addResource(attributeInstanceResource);
    }

    @Test
    public void testAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeInstance:update")).thenReturn(true);
        assertThat(client().resource("/api/1/attributeInstances/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(AttributeInstance.class, attributeInstance)).isEqualTo(attributeInstance);
    }

    @Test
    public void testUnAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeInstance:update")).thenReturn(false);
        assertEquals(401, client().resource("/api/1/attributeInstances/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, attributeInstance).getStatus());
    }

    @Test
    public void testAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeInstance:create")).thenReturn(true);
        assertThat(client().resource("/api/1/attributeInstances").type(MediaType.APPLICATION_JSON_TYPE).post(AttributeInstance.class, attributeInstance)).isEqualTo(attributeInstance);
    }

    @Test
    public void testUnAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeInstance:create")).thenReturn(false);
        assertEquals(401, client().resource("/api/1/attributeInstances").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, attributeInstance).getStatus());
    }

    @Test
    public void testAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeInstance:list")).thenReturn(true);
        assertThat(client().resource("/api/1/attributeInstances").get(PaginatedResult.class)).isEqualTo(expectedResult);
    }

    @Test
    public void testUnAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeInstance:list")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/attributeInstances").get(PaginatedResult.class)).isEqualTo(expectedResult);
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }

    @Test
    public void testAuthorizedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeInstance:delete")).thenReturn(true);
        ClientResponse response = client().resource("/api/1/attributeInstances/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertThat(response.getStatus()).isEqualTo(200);

        verify(dao).delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeInstance:delete")).thenReturn(false);
        ClientResponse response = client().resource("/api/1/attributeInstances/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    public void testAuthorizedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeInstance:get")).thenReturn(true);
        assertThat(client().resource("/api/1/attributeInstances/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(AttributeInstance.class))
                .isEqualTo(attributeInstance);
        verify(dao).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeInstances:get")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/attributeInstances/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(AttributeInstance.class));
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }
}