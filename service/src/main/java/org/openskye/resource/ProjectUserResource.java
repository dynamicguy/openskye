package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.domain.ProjectUser;
import org.openskye.domain.Role;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.ProjectUserDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.openskye.domain.UserRole}
 */
@Api(value = "/api/1/projectUsers", description = "Manage project users")
@Path("/api/1/projectUsers")
public class ProjectUserResource extends AbstractUpdatableDomainResource<ProjectUser> {

    private ProjectUserDAO projectUserDAO;

    @Inject
    public ProjectUserResource(ProjectUserDAO projectUserDAO){
        this.projectUserDAO=projectUserDAO;
    }


    @ApiOperation(value = "List all project users", notes = "Returns all user roles in a paginated structure", responseContainer = "List", response = ProjectUser.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<ProjectUser> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Add new project user", response = ProjectUser.class)
    @POST
    @Transactional
    @Timed
    @Override
    public ProjectUser create(ProjectUser newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Delete project user", notes = "Delete project user")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
    }

    @ApiOperation(value = "Get roles for this project user", notes = "Get roles for this project user")
    @Path("/{id}/roles")
    @GET
    @Transactional
    @Timed
    public PaginatedResult<Role> getProjectUserRoles(@PathParam("id") String id){
        ProjectUser projectUser = get(id);
        return new PaginatedResult<Role>().paginate(projectUser.getProjectUserRoles());
    }

    @Override
    protected AbstractPaginatingDAO<ProjectUser> getDAO() {
        return projectUserDAO;
    }

    @Override
    public String getPermissionDomain() {
        return "projectUser";
    }

}
