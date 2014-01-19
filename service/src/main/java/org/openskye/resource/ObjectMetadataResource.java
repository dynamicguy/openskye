package org.openskye.resource;


import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.SearchPage;
import org.openskye.core.SimpleObject;
import org.openskye.domain.Domain;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.Project;
import org.openskye.domain.Task;
import org.openskye.domain.dao.*;
import org.openskye.exceptions.BadRequestException;
import org.openskye.exceptions.NotFoundException;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * This resource creates an API which deals with {@link ObjectMetadata}. It
 * allows access to both the {@link ObjectMetadataRepository} and the
 * {@link ObjectMetadataSearch} instances, allowing the user to create, index,
 * search, and manage {@link ObjectMetadata}.
 */
@Api(value = "/api/1/objects", description = "Access and act upon ObjectMetadata.")
@Path("/api/1/objects")
@Produces(MediaType.APPLICATION_JSON)
public class ObjectMetadataResource {

    private ObjectMetadataRepository repository;
    private ObjectMetadataSearch search;
    private InformationStoreDefinitionDAO informationStores;
    private DomainDAO domains;
    private TaskDAO tasks;
    private ProjectDAO projects;

    @Inject
    public ObjectMetadataResource(ObjectMetadataRepository repository, ObjectMetadataSearch search, InformationStoreDefinitionDAO informationStores, DomainDAO domains, TaskDAO tasks, ProjectDAO projects) {
        this.repository = repository;
        this.search = search;
        this.informationStores = informationStores;
        this.domains = domains;
        this.tasks = tasks;
        this.projects = projects;
    }

    @ApiOperation(value = "Index ObjectMetadata",
            notes = "Provide the ObjectMetadata id.  It is retrieved and the index is either created or updated.")
    @Path("/index/{id}")
    @PUT
    @Transactional
    @Timed
    public Response index(@ApiParam(value = "The id of the ObjectMetadata", required = true)
                          @PathParam("id")
                          String id) {


        Optional<ObjectMetadata> metadata = repository.get(id);

        if (!metadata.isPresent())
            throw new NotFoundException();
        checkPermission("index", metadata.get().getProject().getId());

        search.index(metadata.get());

        return Response.ok().build();
    }

    /**
     * Updates the {@link ObjectMetadata}.  Note that the index will also
     * be updated upon updating.
     *
     * @param id          The id of the ObjectMetadata to update.
     * @param newInstance The requested updates to the {@link ObjectMetadata}
     * @return The updated {@link ObjectMetadata}.
     */
    @ApiOperation(value = "Update ObjectMetadata",
            notes = "Enter the id of the ObjectMetadata and the new information to update.  Returns the updated ObjectMetadata",
            response = ObjectMetadata.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    public ObjectMetadata update(@PathParam("id") String id, ObjectMetadata newInstance) {
        checkPermission("update", newInstance.getProject().getId());

        if (!id.equals(newInstance.getId()))
            throw new BadRequestException();

        Optional<ObjectMetadata> oldInstance = repository.get(id);

        if (!oldInstance.isPresent())
            throw new NotFoundException();

        repository.put(newInstance);

        return newInstance;
    }

    @ApiOperation(value = "Gets the ObjectMetadata for the id",
            notes = "Returns the ObjectMetadata for the id",
            response = ObjectMetadata.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    public ObjectMetadata get(@PathParam("id") String id) {

        Optional<ObjectMetadata> metadata = repository.get(id);

        if (!metadata.isPresent())
            throw new NotFoundException();
        checkPermission("get", metadata.get().getProject().getId());


        return metadata.get();
    }

    /**
     * Gets a list of the {@link ArchiveContentBlock} instances
     * for the {@link SimpleObject}.
     *
     * @param id The id of the {@link ObjectMetadata} for the object.
     * @return The {@link PaginatedResult} containing all the blocks.
     */
    @ApiOperation(value = "Get content blocks for simple object with id",
            notes = "Return content block meta data",
            responseContainer = "List",
            response = ArchiveContentBlock.class)
    @Path("/{id}/blocks")
    @GET
    @Transactional
    @Timed
    public PaginatedResult<ArchiveContentBlock> getContentBlocks(@PathParam("id") String id) {
        Optional<ObjectMetadata> metadata = repository.get(id);
        if (!metadata.isPresent())
            throw new NotFoundException();
        checkPermission("get", metadata.get().getProject().getId());

        return new PaginatedResult<>(metadata.get().getArchiveContentBlocks());
    }

    /**
     * Gets a list of all {@link ObjectMetadata} instances.
     *
     * @return A {@link PaginatedResult} listing all instances.
     */
    @ApiOperation(value = "List all ObjectMetadata",
            notes = "Returns all ObjectMetadata in a paginated structure",
            responseContainer = "List",
            response = ObjectMetadata.class)
    @GET
    @Transactional
    @Timed
    public PaginatedResult<ObjectMetadata> getAll() {
        checkPermission("list","*");
        List<ObjectMetadata> result = new ArrayList<>();
        for (ObjectMetadata objectMetadata : repository.getAllObjects()) {
            if (SecurityUtils.getSubject().isPermitted("objects:list:" + objectMetadata.getProject().getId())) {
                result.add(objectMetadata);
            }
        }

        return new PaginatedResult<>(result);
    }

    /**
     * Lists all {@link ObjectMetadata} for an InformationStore.
     *
     * @param isdId The id of the {@link InformationStoreDefinition} for the query.
     * @return A {@link PaginatedResult} listing all instances related to the store.
     */
    @ApiOperation(value = "Lists all ObjectMetadata for an InformationStore",
            notes = "Supply an InformationStoreDefinition id.  Returns a list of ObjectMetadata in a paginated structure",
            responseContainer = "List",
            response = ObjectMetadata.class)
    @Path("/informationStore/{isdId}")
    @GET
    @Transactional
    @Timed
    public PaginatedResult<ObjectMetadata> getByInformationStore(@PathParam("isdId") String isdId) {

        Optional<InformationStoreDefinition> isd = informationStores.get(isdId);

        if (!isd.isPresent())
            throw new NotFoundException();

        checkPermission("list", isd.get().getProject().getId());


        return new PaginatedResult<>(repository.getObjects(isd.get()));
    }

    /**
     * Lists all {@link ObjectMetadata} related to the given {@link Task} id.
     *
     * @param taskId The id of the {@link Task} for the query.
     * @return A {@link PaginatedResult} listing all instances related to the {@link Task}.
     */
    @ApiOperation(value = "Lists all ObjectMetadata for a Task",
            notes = "Supply a Task id.  Returns a list of ObjectMetadata in a paginated structure",
            responseContainer = "List",
            response = ObjectMetadata.class)
    @Path("/task/{taskId}")
    @GET
    @Transactional
    @Timed
    public PaginatedResult<ObjectMetadata> getByTask(@PathParam("taskId") String taskId) {

        Optional<Task> task = tasks.get(taskId);

        if (!task.isPresent())
            throw new NotFoundException();
        checkPermission("list", task.get().getProject().getId());

        return new PaginatedResult<>(repository.getObjects(task.get()));

    }

    /**
     * Searches for {@link ObjectMetadata} across all {@link Project} instances
     * for the given {@link Domain} id.
     *
     * @param query The query to be performed.
     * @return A {@link PaginatedResult} containing the specified
     *         of the search requested.
     */
    @ApiOperation(value = "Searches for ObjectMetadata",
            notes = "Supply a query string (query).  Returns a list of ObjectMetadata in a paginated structure",
            responseContainer = "List",
            response = ObjectMetadata.class)
    @Path("/search")
    @GET
    @Transactional
    @Timed
    public PaginatedResult<ObjectMetadata> search(@ApiParam(value = "The query string", required = true)
                                                  @QueryParam("query")
                                                  String query) {
        checkPermission("search","*");

        SearchPage searchPage = new SearchPage(1, 1000);

        List<ObjectMetadata> results = Lists.newArrayList(search.search(query, searchPage));
        for(ObjectMetadata om : results){
            if(!SecurityUtils.getSubject().isPermitted("objects:search:"+om.getProject().getId())){
                results.remove(om);
            }
        }

        return new PaginatedResult<>(results);

    }

    /**
     * Searches for {@link ObjectMetadata} for a specific {@link Project} within
     * the specified {@link Domain}.
     *
     * @param projectId The id of the {@link Project} to be searched.
     * @param query     The query to be performed.
     * @return A {@link PaginatedResult} containing the specified
     *         of the search requested.
     */
    @ApiOperation(value = "Searches for ObjectMetadata",
            notes = "Supply a  a Project id, a query string (query),  " +
                    "Returns a list of ObjectMetadata in a paginated structure",
            responseContainer = "List",
            response = ObjectMetadata.class)
    @Path("/search/{projectId}")
    @GET
    @Transactional
    @Timed
    public PaginatedResult<ObjectMetadata> search(@PathParam("projectId") String projectId,
                                                  @ApiParam(value = "The query string", required = true)
                                                  @QueryParam("query")
                                                  String query) {
        checkPermission("search", projectId);

        SearchPage searchPage = new SearchPage(1, 1000);

        Optional<Project> project = projects.get(projectId);

        if (!project.isPresent())
            throw new NotFoundException();

        return new PaginatedResult<>(search.search(project.get(), query, searchPage));
    }

    private void checkPermission(String operation, String projectId) {
        if (!SecurityUtils.getSubject().isPermitted("objects:" + operation + ":" + projectId)) {
            throw new UnauthorizedException();
        }
    }
}
