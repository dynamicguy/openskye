package org.openskye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.apache.shiro.util.ThreadContext;
import org.junit.ClassRule;
import org.junit.Test;
import org.openskye.domain.Project;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.ProjectDAO;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectResourceTest extends AbstractResourceTest<Project> {

    public static final ProjectDAO dao = mock(ProjectDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new ProjectResource(dao))
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
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
    @Test
    public void testAuthorizedPut() throws Exception{
        ThreadContext.bind(subject);
        when(subject.isPermitted(getSingular()+":update:59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(subject.isPermitted(getSingular()+":get:59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        assertThat(getResources().client().resource("/api/1/" + getPlural() + "/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(entityClass, getInstance()), equalTo(getInstance()));
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