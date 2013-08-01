package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import org.skye.domain.Domain;
import org.skye.domain.Project;
import org.skye.resource.dao.AbstractPaginatingDAO;

import javax.ws.rs.Path;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/projects", description = "Manage projects")
@Path("/api/1/projects")
public class ProjectResource extends AbstractUpdatableDomainResource<Project> {

    public ProjectResource(AbstractPaginatingDAO<Project> dao) {
        super(dao);
    }
}
