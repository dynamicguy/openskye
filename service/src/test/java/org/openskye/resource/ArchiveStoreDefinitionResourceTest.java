package org.openskye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.ArchiveStoreDefinitionDAO;
import org.openskye.domain.dao.AttributeDefinitionDAO;
import org.openskye.domain.dao.PaginatedResult;

import static org.mockito.Mockito.mock;

public class ArchiveStoreDefinitionResourceTest extends AbstractResourceTest<ArchiveStoreDefinition> {

    public static ArchiveStoreDefinitionDAO dao = mock(ArchiveStoreDefinitionDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new ArchiveStoreDefinitionResource(dao))
            .build();

    private final ArchiveStoreDefinition archiveStoreDefinition=new ArchiveStoreDefinition();
    private PaginatedResult<ArchiveStoreDefinition> expectedResult=new PaginatedResult<>();

    @Override
    public String getSingular() {
        return "archiveStoreDefinition";
    }

    @Override
    public String getPlural() {
        return "archiveStoreDefinitions";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public ArchiveStoreDefinition getInstance() {
        return archiveStoreDefinition;
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
