package org.skye.resource;

import org.skye.domain.Domain;
import org.skye.domain.Project;
import org.skye.resource.dao.AbstractPaginatingDAO;

import javax.ws.rs.Path;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Path("/api/1/projects")
public class ProjectResource extends AbstractUpdatableDomainResource<Project> {

    public ProjectResource(AbstractPaginatingDAO<Project> dao) {
        super(dao);
    }
}
