package org.skye.resource;

import org.skye.domain.Project;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.ProjectDAO;

import javax.inject.Inject;
import javax.ws.rs.Path;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Path("/api/1/projects")
public class ProjectResource extends AbstractUpdatableDomainResource<Project> {

    @Inject
    protected ProjectDAO projectDAO;

    @Override
    protected AbstractPaginatingDAO<Project> getDAO() {
        return projectDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "project";
    }
}
