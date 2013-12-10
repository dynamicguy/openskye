package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.openskye.domain.*;
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
        newInstance.setDomain(getCurrentUser().getDomain());
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update project", notes = "Enter the id of the project to update, return the updated project", response = Project.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public Project update(@PathParam("id") String id, Project newInstance) {
        if (isPermitted("update", id)) {
            return super.update(id, newInstance);
        } else {
            throw new UnauthorizedException();
        }
    }

    @ApiOperation(value = "Find project by id", notes = "Return a project by its unique id", response = Project.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public Project get(@PathParam("id") String id) {
        if (isPermitted("get", id))
            return super.get(id);
        else {
            throw new UnauthorizedException();
        }
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
        if (isPermitted("delete", id)) {
            return super.delete(id);
        } else {
            throw new UnauthorizedException();
        }
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
        if (isPermitted("get", id)) {
            return new PaginatedResult<Channel>().paginate(project.getChannels());
        } else {
            throw new UnauthorizedException();
        }
    }

    @Path("/{id}/archiveStores")
    @GET
    @ApiOperation(value = "Return the archive stores owned by this domain")
    public PaginatedResult<ArchiveStoreDefinition> getArchiveStores(@PathParam("id") String id) {
        Project project = get(id);
        if (isPermitted("get", id)) {
            return new PaginatedResult<ArchiveStoreDefinition>().paginate(project.getArchiveStores());
        } else {
            throw new UnauthorizedException();
        }
    }

    @Path("/{id}/informationStores")
    @GET
    @ApiOperation(value = "Return the information stores owned by this domain")
    public PaginatedResult<InformationStoreDefinition> getInformationStores(@PathParam("id") String id) {
        Project project = get(id);
        if (isPermitted("get", id)) {
            return new PaginatedResult<InformationStoreDefinition>().paginate(project.getInformationStores());
        } else {
            throw new UnauthorizedException();
        }
    }

    @Path("/{id}/users")
    @GET
    @ApiOperation(value = "Return the users associated with this project")
    public PaginatedResult<ProjectUser> getProjectUsers(@PathParam("id") String id) {
        Project project = get(id);
        if (isPermitted("get", id)) {
            return new PaginatedResult<ProjectUser>().paginate(project.getProjectUsers());
        } else {
            throw new UnauthorizedException();
        }
    }

    public boolean isPermitted(String action, String projectId) {
        return SecurityUtils.getSubject().isPermitted(getPermissionDomain() + ":" + action + ":" + projectId);
    }


}
