package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.domain.Channel;
import org.openskye.domain.Project;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.ProjectDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.openskye.domain.Domain}
 */
@Api(value = "/api/1/projects", description = "Manage projects")
@Path("/api/1/projects")
public class ProjectResource extends AbstractUpdatableDomainResource<Project> {

    private ProjectDAO projectDAO;

    @Inject
    public ProjectResource(ProjectDAO dao) {
        this.projectDAO = dao;
    }

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

    @ApiOperation(value = "List all projects", notes = "Returns all projects in a paginated structure", responseContainer = "List", response = Project.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<Project> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete project", notes = "Deletes the project(found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
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
