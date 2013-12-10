package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.domain.*;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.ProjectDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * The REST endpoint for {@link org.openskye.domain.Domain}
 */
@Api(value = "/api/1/projects", description = "Manage projects")
@Path("/api/1/projects")
public class ProjectResource extends ProjectSpecificResource<Project> {

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
        projectID="";
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update project", notes = "Enter the id of the project to update, return the updated project", response = Project.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public Project update(@PathParam("id") String id, Project newInstance) {
        projectID = id;
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find project by id", notes = "Return a project by its unique id", response = Project.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public Project get(@PathParam("id") String id) {
        projectID = id;
        return super.get(id);
    }

    @ApiOperation(value = "List all projects", notes = "Returns all projects in a paginated structure", responseContainer = "List", response = Project.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<Project> getAll() {
        projectID="";
        PaginatedResult<Project> projectPaginatedResult = super.getAll();
        List<Project> results = projectPaginatedResult.getResults();
        for(Project p : results){
            if(!isPermitted("list",p.getId())){
                results.remove(p);
            }
        }
        projectPaginatedResult.setResults(results);
        return projectPaginatedResult;
    }

    @ApiOperation(value = "Delete project", notes = "Deletes the project(found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        projectID = id;
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
        projectID = id;
        return new PaginatedResult<Channel>().paginate(project.getChannels());
    }

    @Path("/{id}/archiveStores")
    @GET
    @ApiOperation(value = "Return the archive stores owned by this domain")
    public PaginatedResult<ArchiveStoreDefinition> getArchiveStores(@PathParam("id") String id) {
        Project project = get(id);
        projectID = id;
        return new PaginatedResult<ArchiveStoreDefinition>().paginate(project.getArchiveStores());
    }

    @Path("/{id}/informationStores")
    @GET
    @ApiOperation(value = "Return the information stores owned by this domain")
    public PaginatedResult<InformationStoreDefinition> getInformationStores(@PathParam("id") String id) {
        Project project = get(id);
        projectID = id;
        return new PaginatedResult<InformationStoreDefinition>().paginate(project.getInformationStores());
    }

    @Path("/{id}/users")
    @GET
    @ApiOperation(value = "Return the users associated with this project")
    public PaginatedResult<ProjectUser> getProjectUsers(@PathParam("id") String id) {
        Project project = get(id);
        projectID = id;
        return new PaginatedResult<ProjectUser>().paginate(project.getProjectUsers());
    }

}
