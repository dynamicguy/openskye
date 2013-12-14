package org.openskye.resource;


import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.Project;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.InformationStoreDefinitionDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import static org.mockito.Mockito.mock;

public class InformationStoreDefinitionResourceTest extends ProjectSpecificResourceTest<InformationStoreDefinition> {


    public static final InformationStoreDefinitionDAO dao=mock(InformationStoreDefinitionDAO.class);
    @ClassRule
    public static final ResourceTestRule resources= ResourceTestRule.builder()
            .addResource(new InformationStoreDefinitionResource(dao))
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
            .build();

    private final InformationStoreDefinition informationStoreDefinition=new InformationStoreDefinition();
    private PaginatedResult<InformationStoreDefinition> expectedResult=new PaginatedResult<>();

    @Override
    public void setUp(){
        super.setUp();
        Project project = new Project();
        project.setId(projectID);
        informationStoreDefinition.setProject(project);
    }

    @Override
    public String getSingular() {
        return "informationStoreDefinition";
    }

    @Override
    public String getPlural() {
        return "informationStoreDefinitions";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public InformationStoreDefinition getInstance() {
        return informationStoreDefinition;
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
