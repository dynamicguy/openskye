package org.skye.resource;

import com.yammer.dropwizard.testing.ResourceTest;
import org.junit.Test;
import org.skye.domain.Domain;
import org.skye.resource.dao.DomainDAO;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class DomainResourceTest extends ResourceTest {
    private final Domain domain = new Domain();
    private final DomainDAO dao = mock(DomainDAO.class);

    @Override
    protected void setUpResources() {
        when(dao.get(anyString())).thenReturn(domain);
        DomainResource domainResource = new DomainResource();
        domainResource.domainDAO = dao;
        addResource(domainResource);
    }

    @Test
    public void simpleResourceTest() throws Exception {
        assertThat(client().resource("/api/1/domains/1").get(Domain.class))
                .isEqualTo(domain);
        verify(dao).get("1");
    }
}