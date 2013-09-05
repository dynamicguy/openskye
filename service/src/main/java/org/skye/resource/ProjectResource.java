package org.skye.resource;

import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.skye.domain.Channel;
import org.skye.domain.Project;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.ProjectDAO;
import org.skye.util.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.*;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/projects", description = "Manage projects")
@Path("/api/1/projects")
public class ProjectResource extends AbstractUpdatableDomainResource<Project> {

    @Inject
    protected ProjectDAO projectDAO;

    @ApiOperation(value = "Create new project", notes = "Create a new project and return with its unique id", response = Project.class)
    @POST
    @Transactional
    @Timed
    public Project create(Project newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update project", notes = "Enter the id of the project to update, return the updated project", response = Project.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public Project update(@PathParam("id") String id, Project newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find project by id", notes = "Return a project by its unique id", response = Project.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public Project get(@PathParam("id") String id) {
        return super.get(id);
    }

    @Override
    protected AbstractPaginatingDAO<Project> getDAO() {
        return projectDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "project";
    }

    @Path("/{id}/channels")
    @GET
    @ApiOperation(value = "Return the channels for this project")
    public PaginatedResult<Channel> getChannels(@PathParam("id") String id) {
        Project project = get(id);
        return new PaginatedResult<Channel>().paginate(project.getChannels());
    }

}
