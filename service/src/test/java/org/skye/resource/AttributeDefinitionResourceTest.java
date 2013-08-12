package org.skye.resource;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.AttributeDefinition;
import org.skye.domain.AttributeInstance;
import org.skye.resource.dao.AttributeDefinitionDAO;
import org.skye.resource.dao.AttributeInstanceDAO;
import org.skye.util.PaginatedResult;

import javax.ws.rs.core.MediaType;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AttributeDefinitionResourceTest extends ResourceTest {
    private final AttributeDefinition attributeDefinition = new AttributeDefinition();
    private final AttributeDefinitionDAO dao = mock(AttributeDefinitionDAO.class);
    private final Subject subject = mock(Subject.class);
    private PaginatedResult<AttributeDefinition> expectedResult = new PaginatedResult<>();

    @Override
    protected void setUpResources() {
        when(dao.list()).thenReturn(expectedResult);
        when(dao.delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(attributeDefinition);
        when(dao.persist(attributeDefinition)).thenReturn(attributeDefinition);
        AttributeDefinitionResource attributeDefinitionResource = new AttributeDefinitionResource();
        attributeDefinitionResource.attributeDefinitionDAO = dao;
        addResource(attributeDefinitionResource);
    }

    @Test
    public void testAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeDefinition:update")).thenReturn(true);
        assertThat(client().resource("/api/1/attributeDefinitions/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(AttributeDefinition.class, attributeDefinition)).isEqualTo(attributeDefinition);
    }

    @Test
    public void testUnAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeDefinition:update")).thenReturn(false);
        assertEquals(401,client().resource("/api/1/attributeDefinitions/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, attributeDefinition).getStatus());
    }

    @Test
    public void testAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeDefinition:create")).thenReturn(true);
        assertThat(client().resource("/api/1/attributeDefinitions").type(MediaType.APPLICATION_JSON_TYPE).post(AttributeDefinition.class, attributeDefinition)).isEqualTo(attributeDefinition);
    }

    @Test
    public void testUnAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeDefinition:create")).thenReturn(false);
        assertEquals(401,client().resource("/api/1/attributeDefinitions").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, attributeDefinition).getStatus());
    }

    @Test
    public void testAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeDefinition:list")).thenReturn(true);
        assertThat(client().resource("/api/1/attributeDefinitions").get(PaginatedResult.class)).isEqualTo(expectedResult);
    }

    @Test
    public void testUnAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeDefinition:list")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/attributeDefinitions").get(PaginatedResult.class)).isEqualTo(expectedResult);
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }

    @Test
    public void testAuthorizedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeDefinition:delete")).thenReturn(true);
        ClientResponse response = client().resource("/api/1/attributeDefinitions/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertEquals(200,response.getStatus());

        verify(dao).delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }
    @Test
    public void testUnAuthorisedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeDefinition:delete")).thenReturn(false);
        ClientResponse response = client().resource("/api/1/attributeDefinitions/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertEquals(401,response.getStatus());
    }

    @Test
    public void testAuthorizedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeDefinition:get")).thenReturn(true);
        assertThat(client().resource("/api/1/attributeDefinitions/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(AttributeDefinition.class))
                .isEqualTo(attributeDefinition);
        verify(dao).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("attributeDefinitions:get")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/attributeDefinitions/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(AttributeDefinition.class));
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }
}