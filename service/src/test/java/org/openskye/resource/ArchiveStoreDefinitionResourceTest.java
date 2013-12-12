package org.openskye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.Project;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.ArchiveStoreDefinitionDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import static org.mockito.Mockito.mock;

public class ArchiveStoreDefinitionResourceTest extends ProjectSpecificResourceTest<ArchiveStoreDefinition> {

    public static final ArchiveStoreDefinitionDAO dao = mock(ArchiveStoreDefinitionDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new ArchiveStoreDefinitionResource(dao))
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
            .build();

    private ArchiveStoreDefinition archiveStoreDefinition=new ArchiveStoreDefinition();
    private PaginatedResult<ArchiveStoreDefinition> expectedResult=new PaginatedResult<>();


    @Override
    public void setUp(){
        super.setUp();
        Project project = new Project();
        project.setId(projectID);
        archiveStoreDefinition.setProject(project);
    }

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
