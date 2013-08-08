package org.skye.resource;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.Domain;
import org.skye.resource.dao.DomainDAO;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class DomainResourceTest extends ResourceTest {
    private final Domain domain = new Domain();
    private final DomainDAO dao = mock(DomainDAO.class);
    private final Subject subject = mock(Subject.class);

    @Override
    protected void setUpResources() {
        when(dao.get(anyString())).thenReturn(domain);
        DomainResource domainResource = new DomainResource();
        domainResource.domainDAO = dao;
        addResource(domainResource);
    }

    @Test
    public void testAuthorizedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("domain:get")).thenReturn(true);
        assertThat(client().resource("/api/1/domains/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(Domain.class))
                .isEqualTo(domain);
        verify(dao).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted(anyString())).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/domains/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(Domain.class));
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }
}