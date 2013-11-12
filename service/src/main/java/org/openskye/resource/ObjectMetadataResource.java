package org.openskye.resource;


import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.openskye.core.*;
import org.openskye.domain.*;
import org.openskye.domain.dao.*;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;
import org.openskye.stores.StoreRegistry;
import org.openskye.exceptions.BadRequestException;
import org.openskye.exceptions.NotFoundException;
import org.apache.shiro.authz.UnauthorizedException;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.util.Page;

import java.io.InputStream;

/**
 * This resource creates an API which deals with {@link ObjectMetadata}. It
 * allows access to both the {@link ObjectMetadataRepository} and the
 * {@link ObjectMetadataSearch} instances, allowing the user to create, index,
 * search, and manage {@link ObjectMetadata}.
 */
@Api(value = "/api/1/objects", description = "Access and act upon ObjectMetadata.")
@Path("/api/1/objects")
@Produces(MediaType.APPLICATION_JSON)
public class ObjectMetadataResource
{
    private ObjectMetadataRepository repository;

    private ObjectMetadataSearch search;

    private StoreRegistry registry;

    private InformationStoreDefinitionDAO informationStores;

    private DomainDAO domains;

    private TaskDAO tasks;

    private ProjectDAO projects;

    public static final String OPERATION_GET = "objects:get";
    public static final String OPERATION_CREATE = "objects:create";
    public static final String OPERATION_UPDATE = "objects:update";
    public static final String OPERATION_INDEX = "objects:index";
    public static final String OPERATION_LIST = "objects:list";
    public static final String OPERATION_SEARCH = "objects:search";

    @Inject
    public ObjectMetadataResource(ObjectMetadataRepository injectedRepository, ObjectMetadataSearch injectedSearch)
    {
        repository = injectedRepository;
        search = injectedSearch;
    }

    @Inject
    public ObjectMetadataResource setStoreRegistry(StoreRegistry injectedRegistry)
    {
        registry = injectedRegistry;

        return this;
    }

    @Inject
    public ObjectMetadataResource setInformationStoreDefinitionDAO(InformationStoreDefinitionDAO injectedDao)
    {
        informationStores = injectedDao;

        return this;
    }

    @Inject
    public ObjectMetadataResource setDomainDAO(DomainDAO injectedDao)
    {
        domains = injectedDao;

        return this;
    }

    @Inject
    public ObjectMetadataResource setTaskDAO(TaskDAO injectedDao)
    {
        tasks = injectedDao;

        return this;
    }

    @Inject
    public ObjectMetadataResource setProjectDAO(ProjectDAO injectedDao)
    {
        projects = injectedDao;

        return this;
    }

    /**
     * Gets the raw content of the {@link SimpleObject}
     *
     * @param id The id of the {@link ObjectMetadata} for the object.
     *
     * @return A Response containing the raw content of the {@link SimpleObject}.
     */
    @ApiOperation(value = "Get the raw content of the simple object", notes = "Return raw content of the simple object")
    @Path("/{id}/content")
    @GET
    @Transactional
    @Timed
    public Response getContent(@PathParam("id") String id)
    {
        Optional<ObjectMetadata> objectMetadata;
        ArchiveStoreDefinition asd;
        Optional<ArchiveStore> archiveStore;
        ArchiveContentBlock block;
        Optional<InputStream> stream;
        String path;
        String header;

        if(!this.isPermitted(OPERATION_GET))
            throw new UnauthorizedException();

        objectMetadata = this.repository.get(id);

        if(!objectMetadata.isPresent())
            throw new NotFoundException();

        if(objectMetadata.get().getArchiveContentBlocks().size() == 0)
            throw new NotFoundException();

        block = objectMetadata.get().getArchiveContentBlocks().get(0);
        asd = this.repository.getArchiveStoreDefinition(block);
        archiveStore = this.registry.build(asd);

        if(!archiveStore.isPresent())
            throw new NotFoundException();

        stream = archiveStore.get().getStream(objectMetadata.get());

        if(!stream.isPresent())
            throw new NotFoundException();

        path = objectMetadata.get().getPath();
        header = "attachment; filename=" + path;

        return Response.ok(stream.get())
                       .header("Content-Disposition", header)
                       .build();
    }

    /**
     * Creates a new {@link ObjectMetadata}.  Note that the object will be
     * indexed upon creation for searching.
     *
     * @param newInstance The instance to be created.
     *
     * @return The created instance with its id field set.
     */
    @ApiOperation(value = "Create ObjectMetadata",
                  notes = "Creates a new ObjectMetadata and returns with its id",
                  response = ObjectMetadata.class)
    @POST
    @Transactional
    @Timed
    public ObjectMetadata create(ObjectMetadata newInstance)
    {
        if(!this.isPermitted(OPERATION_CREATE))
            throw new UnauthorizedException();

        // If the id field is set on the newInstance, we should set
        // it to null, so that a random id is generated.
        newInstance.setId(null);

        this.repository.put(newInstance);

        this.search.index(newInstance);

        return newInstance;
    }

    @ApiOperation(value = "Index ObjectMetadata",
                  notes = "Provide the ObjectMetadata id.  It is retrieved and the index is either created or updated.")
    @Path("/index/{id}")
    @PUT
    @Transactional
    @Timed
    public Response index(@ApiParam(value = "The id of the ObjectMetadata", required = true)
                          @PathParam("id")
                          String id)
    {
        if(!this.isPermitted(OPERATION_INDEX))
            throw new UnauthorizedException();

        Optional<ObjectMetadata> metadata = this.repository.get(id);

        if(!metadata.isPresent())
            throw new NotFoundException();

        this.search.index(metadata.get());

        return Response.ok().build();
    }

    /**
     * Updates the {@link ObjectMetadata}.  Note that the index will also
     * be updated upon updating.
     *
     * @param id The id of the ObjectMetadata to update.
     *
     * @param newInstance The requested updates to the {@link ObjectMetadata}
     *
     * @return The updated {@link ObjectMetadata}.
     */
    @ApiOperation(value = "Update ObjectMetadata",
                  notes = "Enter the id of the ObjectMetadata and the new information to update.  Returns the updated ObjectMetadata",
                  response = ObjectMetadata.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    public ObjectMetadata update(@PathParam("id") String id, ObjectMetadata newInstance)
    {
        if(!this.isPermitted(OPERATION_UPDATE))
            throw new UnauthorizedException();

        if(!id.equals(newInstance.getId()))
            throw new BadRequestException();

        Optional<ObjectMetadata> oldInstance = this.repository.get(id);

        if(!oldInstance.isPresent())
            throw new NotFoundException();

        this.repository.put(newInstance);

        return newInstance;
    }

    @ApiOperation(value = "Gets the ObjectMetadata for the id",
            notes = "Returns the ObjectMetadata for the id",
            response = ObjectMetadata.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    public ObjectMetadata get(@PathParam("id") String id)
    {
        Optional<ObjectMetadata> metadata;

        if(!this.isPermitted(OPERATION_GET))
            throw new UnauthorizedException();

        metadata = this.repository.get(id);

        if(!metadata.isPresent())
            throw new NotFoundException();

        return metadata.get();
    }

    /**
     * Gets a list of the {@link ArchiveContentBlock} instances
     * for the {@link SimpleObject}.
     *
     * @param id The id of the {@link ObjectMetadata} for the object.
     *
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
    public PaginatedResult<ArchiveContentBlock> getContentBlocks(@PathParam("id") String id)
    {
        Optional<ObjectMetadata> metadata;
        PaginatedResult<ArchiveContentBlock> result;

        if(!this.isPermitted(OPERATION_GET))
            throw new UnauthorizedException();

        metadata = this.repository.get(id);

        if(!metadata.isPresent())
            throw new NotFoundException();

        result = new PaginatedResult<>(metadata.get().getArchiveContentBlocks());

        return result;
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
    public PaginatedResult<ObjectMetadata> getAll()
    {
        PaginatedResult<ObjectMetadata> result;

        if(!this.isPermitted(OPERATION_LIST))
            throw new UnauthorizedException();

        result = new PaginatedResult<>(this.repository.getAllObjects());

        return result;
    }

    /**
     * Lists all {@link ObjectMetadata} for an InformationStore.
     *
     * @param isdId The id of the {@link InformationStoreDefinition} for the query.
     *
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
    public PaginatedResult<ObjectMetadata> getByInformationStore(@PathParam("isdId") String isdId)
    {
        PaginatedResult<ObjectMetadata> result;
        Optional<InformationStoreDefinition> isd;

        if(!this.isPermitted(OPERATION_LIST))
            throw new UnauthorizedException();

        isd = this.informationStores.get(isdId);

        if(!isd.isPresent())
            throw new NotFoundException();

        result = new PaginatedResult<>(this.repository.getObjects(isd.get()));

        return result;
    }

    /**
     * Lists all {@link ObjectMetadata} related to the given {@link Task} id.
     *
     * @param taskId The id of the {@link Task} for the query.
     *
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
    public PaginatedResult<ObjectMetadata> getByTask(@PathParam("taskId") String taskId)
    {
        PaginatedResult<ObjectMetadata> result;
        Optional<Task> task;

        if(!this.isPermitted(OPERATION_LIST))
            throw new UnauthorizedException();

        task = this.tasks.get(taskId);

        if(!task.isPresent())
            throw new NotFoundException();

        result = new PaginatedResult<>(this.repository.getObjects(task.get()));

        return result;
    }

    /**
     * Searches for {@link ObjectMetadata} across all {@link Project} instances
     * for the given {@link Domain} id.
     *
     * @param domainId The id of the {@link Domain} for the search.
     *
     * @param query The query to be performed.
     *
     * @param pageNumber The page number to be returned.
     *
     * @param pageSize The size of the page to be returned.
     *
     * @return A {@link PaginatedResult} containing the specified {@link Page}
     * of the search requested.
     */
    @ApiOperation(value = "Searches for ObjectMetadata",
                  notes = "Supply a Domain id, a query string (query), a page number (pageNumber), and a page size (pageSize).  Returns a list of ObjectMetadata in a paginated structure",
                  responseContainer = "List",
                  response = ObjectMetadata.class)
    @Path("/search/{domainId}")
    @GET
    @Transactional
    @Timed
    public PaginatedResult<ObjectMetadata> search(@PathParam("domainId")
                                                  String domainId,
                                                  @ApiParam(value = "The query string", required = true)
                                                  @QueryParam("query")
                                                  String query,
                                                  @ApiParam(value = "The page number for the results", required = false)
                                                  @QueryParam("pageNumber")
                                                  String pageNumber,
                                                  @ApiParam(value = "The page size for the results", required = false)
                                                  @QueryParam("pageSize")
                                                  String pageSize)
    {
        PaginatedResult<ObjectMetadata> result;
        Optional<Domain> domain;

        if(!this.isPermitted(OPERATION_SEARCH))
            throw new UnauthorizedException();

        domain = this.domains.get(domainId);

        if(!domain.isPresent())
            throw new NotFoundException();

        if(pageNumber == null || pageNumber.isEmpty())
        {
            result = new PaginatedResult<>(this.search.search(domain.get(), query));
        }
        else
        {
            Page page = new Page();

            if(pageSize == null || pageSize.isEmpty())
                throw new SkyeException("A pageNumber is provided with no pageSize");

            try
            {
                page.setPageNumber(Integer.parseInt(pageNumber));
            }
            catch(NumberFormatException ex)
            {
                throw new SkyeException("The pageNumber is not an integer", ex);
            }

            try
            {
                page.setPageSize(Integer.parseInt(pageSize));
            }
            catch(NumberFormatException ex)
            {
                throw new SkyeException("The pageSize is not an integer");
            }

            result = new PaginatedResult<>(this.search.search(domain.get(), query, page));

            result.setPage(page.getPageNumber());
            result.setPageSize(page.getPageSize());
        }

        return result;
    }

    /**
     * Searches for {@link ObjectMetadata} for a specific {@link Project} within
     * the specified {@link Domain}.
     *
     * @param domainId The id of the {@link Domain} for the search.
     *
     * @param projectId The id of the {@link Project} to be searched.
     *
     * @param query The query to be performed.
     *
     * @param pageNumber The page number to be returned.
     *
     * @param pageSize The size of the page to be returned.
     *
     * @return A {@link PaginatedResult} containing the specified {@link Page}
     * of the search requested.
     */
    @ApiOperation(value = "Searches for ObjectMetadata",
                  notes = "Supply a Domain id, a Project id, a query string (query), and optionally, a page number (pageNumber), and a page size(pageSize).  " +
                          "Returns a list of ObjectMetadata in a paginated structure",
                  responseContainer = "List",
                  response = ObjectMetadata.class)
    @Path("/search/{domainId}/{projectId}")
    @GET
    @Transactional
    @Timed
    public PaginatedResult<ObjectMetadata> search(@PathParam("domainId") String domainId,
                                                  @PathParam("projectId") String projectId,
                                                  @ApiParam(value = "The query string", required = true)
                                                  @QueryParam("query")
                                                  String query,
                                                  @ApiParam(value = "The page number for the results", required = false)
                                                  @QueryParam("pageNumber")
                                                  String pageNumber,
                                                  @ApiParam(value = "The page size for the results", required = false)
                                                  @QueryParam("pageSize")
                                                  String pageSize)
    {
        PaginatedResult<ObjectMetadata> result = new PaginatedResult<>();
        Optional<Domain> domain;
        Optional<Project> project;

        if(!this.isPermitted(OPERATION_SEARCH))
            throw new UnauthorizedException();

        domain = this.domains.get(domainId);

        if(!domain.isPresent())
            throw new NotFoundException();

        project = this.projects.get(projectId);

        if(!project.isPresent())
            throw new NotFoundException();

        if(pageNumber == null || pageNumber.isEmpty())
        {
            result = new PaginatedResult<>(this.search.search(domain.get(), project.get(), query));
        }
        else
        {
            Page page = new Page();

            if(pageSize == null || pageSize.isEmpty())
                throw new SkyeException("A pageNumber is provided with no pageSize");

            try
            {
                page.setPageNumber(Integer.parseInt(pageNumber));
            }
            catch(NumberFormatException ex)
            {
                throw new SkyeException("The pageNumber is not an integer", ex);
            }

            try
            {
                page.setPageSize(Integer.parseInt(pageSize));
            }
            catch(NumberFormatException ex)
            {
                throw new SkyeException("The pageSize is not an integer");
            }

            result = new PaginatedResult<>(this.search.search(domain.get(), project.get(), query, page));

            result.setPage(page.getPageNumber());
            result.setPageSize(page.getPageSize());
        }

        return result;
    }

    private boolean isPermitted(String operation)
    {
        return SecurityUtils.getSubject().isPermitted(operation);
    }
}
