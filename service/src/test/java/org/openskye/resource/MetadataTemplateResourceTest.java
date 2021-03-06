package org.openskye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.openskye.domain.MetadataTemplate;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.MetadataTemplateDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import static org.mockito.Mockito.mock;

public class MetadataTemplateResourceTest extends AbstractResourceTest<MetadataTemplate> {
    public static final MetadataTemplateDAO dao = mock(MetadataTemplateDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new MetadataTemplateResource(dao))
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
            .build();
    private final MetadataTemplate metadataTemplate = new MetadataTemplate();
    private PaginatedResult<MetadataTemplate> expectedResult = new PaginatedResult<>();

    @Override
    public String getSingular() {
        return "metadataTemplate";
    }

    @Override
    public String getPlural() {
        return "metadataTemplates";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public MetadataTemplate getInstance() {
        return metadataTemplate;
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