package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import org.skye.domain.Project;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.ProjectDAO;

import javax.inject.Inject;
import javax.ws.rs.Path;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/projects", description = "Manage projects")
@Path("/api/1/projects")
public class ProjectResource extends AbstractUpdatableDomainResource<Project> {

    @Inject
    private ProjectDAO projectDAO;

    @Override
    protected AbstractPaginatingDAO<Project> getDAO() {
        return projectDAO;
    }
}
