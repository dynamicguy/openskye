package org.skye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.skye.domain.Project;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.PaginatedResult;
import org.skye.domain.dao.ProjectDAO;

import static org.mockito.Mockito.mock;

public class ProjectResourceTest extends AbstractResourceTest<Project> {
    private static final ProjectDAO dao = mock(ProjectDAO.class);
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
    public Object getExpectedResult() {
        return expectedResult;
    }
}