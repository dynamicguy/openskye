package org.openskye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.openskye.domain.Project;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.ProjectDAO;

import static org.mockito.Mockito.mock;

public class ProjectResourceTest extends AbstractResourceTest<Project> {

    public static final ProjectDAO dao = mock(ProjectDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new ProjectResource(dao))
            .build();
    private final Project project = new Project();
    private PaginatedResult<Project> expectedResult = new PaginatedResult<>();

    @Override
    public String getSingular() {
        return "project";
    }

    @Override
    public String getPlural() {
        return "projects";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public Project getInstance() {
        return project;
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