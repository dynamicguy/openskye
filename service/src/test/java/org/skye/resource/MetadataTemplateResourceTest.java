package org.skye.resource;

import com.google.common.base.Optional;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.MetadataTemplate;
import org.skye.domain.RetentionPolicy;
import org.skye.resource.dao.MetadataTemplateDAO;
import org.skye.resource.dao.RetentionPolicyDAO;
import org.skye.util.PaginatedResult;

import javax.ws.rs.core.MediaType;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class MetadataTemplateResourceTest extends com.yammer.dropwizard.testing.ResourceTest {
    private final MetadataTemplate metadataTemplate = new MetadataTemplate();
    private final MetadataTemplateDAO dao = mock(MetadataTemplateDAO.class);
    private final Subject subject = mock(Subject.class);
    private PaginatedResult<MetadataTemplate> expectedResult = new PaginatedResult<>();

    @Override
    protected void setUpResources() {
        when(dao.list()).thenReturn(expectedResult);
        when(dao.delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(Optional.of(metadataTemplate));
        when(dao.persist(metadataTemplate)).thenReturn(metadataTemplate);
        MetadataTemplateResource metadataTemplateResource = new MetadataTemplateResource();
        metadataTemplateResource.metadataTemplateDAO = dao;
        addResource(metadataTemplateResource);
    }

    @Test
    public void testAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("metadataTemplate:update")).thenReturn(true);
        assertThat(client().resource("/api/1/metadataTemplates/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(MetadataTemplate.class, metadataTemplate)).isEqualTo(metadataTemplate);
    }

    @Test
    public void testUnAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("metadataTemplate:update")).thenReturn(false);
        assertEquals(401,client().resource("/api/1/metadataTemplates/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, metadataTemplate).getStatus());
    }

    @Test
    public void testAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("metadataTemplate:create")).thenReturn(true);
        assertThat(client().resource("/api/1/metadataTemplates").type(MediaType.APPLICATION_JSON_TYPE).post(MetadataTemplate.class, metadataTemplate)).isEqualTo(metadataTemplate);
    }

    @Test
    public void testUnAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("metadataTemplate:create")).thenReturn(false);
        assertEquals(401,client().resource("/api/1/metadataTemplates").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, metadataTemplate).getStatus());
    }

    @Test
    public void testAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("metadataTemplate:list")).thenReturn(true);
        assertThat(client().resource("/api/1/metadataTemplates").get(PaginatedResult.class)).isEqualTo(expectedResult);
    }

    @Test
    public void testUnAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("metadataTemplate:list")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/metadataTemplates").get(PaginatedResult.class)).isEqualTo(expectedResult);
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }

    @Test
    public void testAuthorizedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("metadataTemplate:delete")).thenReturn(true);
        ClientResponse response = client().resource("/api/1/metadataTemplates/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertEquals(200,response.getStatus());

        verify(dao).delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }
    @Test
    public void testUnAuthorisedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("metadataTemplate:delete")).thenReturn(false);
        ClientResponse response = client().resource("/api/1/metadataTemplates/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertEquals(401,response.getStatus());
    }

    @Test
    public void testAuthorizedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("metadataTemplate:get")).thenReturn(true);
        assertThat(client().resource("/api/1/metadataTemplates/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(MetadataTemplate.class))
                .isEqualTo(metadataTemplate);
        verify(dao).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("metadataTemplate:get")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/metadataTemplates/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(MetadataTemplate.class));
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }
}