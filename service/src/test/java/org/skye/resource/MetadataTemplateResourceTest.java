package org.skye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.skye.domain.MetadataTemplate;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.MetadataTemplateDAO;
import org.skye.domain.dao.PaginatedResult;

import static org.mockito.Mockito.mock;

public class MetadataTemplateResourceTest extends AbstractResourceTest<MetadataTemplate> {
    public static final MetadataTemplateDAO dao = mock(MetadataTemplateDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new MetadataTemplateResource(dao))
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