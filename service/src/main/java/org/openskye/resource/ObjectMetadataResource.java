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
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;
import org.openskye.stores.StoreRegistry;
import org.openskye.util.NotFoundException;
import org.openskye.util.UnauthorizedException;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.util.Page;

import java.io.InputStream;
import java.util.List;

/**
 * This resource creates an API which deals with {@link ObjectMetadata}. It
 * allows access to both the {@link ObjectMetadataRepository} and the
 * {@link ObjectMetadataSearch} instances, allowing the user to create, index,
 * search, and manage {@link ObjectMetadata}.
 */
@Api(value = "/api/1/objectMetadata", description = "Access and act upon ObjectMetadata.")
@Path("/api/1/objectMetadata")
@Produces(MediaType.APPLICATION_JSON)
public class ObjectMetadataResource
{
    @Inject
    private ObjectMetadataRepository repository;

    @Inject
    private ObjectMetadataSearch search;

    @Inject
    private StoreRegistry registry;

    public static final String OPERATION_GET = "objectMetadata:get";

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

        if(!SecurityUtils.getSubject().isPermitted(OPERATION_GET))
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
        return null;
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
        return null;
    }

    @ApiOperation(value = "Gets the ObjectMetadata for the id",
            notes = "Returns the ObjectMetadata for the id",
            response = ObjectMetadata.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    public Optional<ObjectMetadata> get(@PathParam("id") String id)
    {
        if(!SecurityUtils.getSubject().isPermitted(OPERATION_GET))
            throw new UnauthorizedException();

        return this.repository.get(id);
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
                  notes = "Return content block meta data"
                  responseContainer = "List",
                  response = ArchiveContentBlock.class)
    @Path("/{id}/blocks")
    @GET
    @Transactional
    @Timed
    public PaginatedResult<ArchiveContentBlock> getContentBlocks(@PathParam("id") String id)
    {
        Optional<ObjectMetadata> metadata;
        PaginatedResult<ArchiveContentBlock> result = new PaginatedResult<>();
        List<ArchiveContentBlock> blocks;

        if(!SecurityUtils.getSubject().isPermitted(OPERATION_GET))
            throw new UnauthorizedException();

        metadata = this.repository.get(id);

        if(!metadata.isPresent())
            throw new NotFoundException();

        blocks = metadata.get().getArchiveContentBlocks();

        result.setTotalResults(blocks.size());
        result.setResults(blocks);

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
        return null;
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
        return null;
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
        return null;
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
    public PaginatedResult<ObjectMetadata> search(@PathParam("domainId") String domainId,
                                                  @ApiParam(value = "The query string", required = true) @QueryParam("query") String query,
                                                  @ApiParam(value = "The page number for the results", required = true) @QueryParam("pageNumber") String pageNumber,
                                                  @ApiParam(value = "The page size for the results", required = true) @QueryParam("pageSize") String pageSize)
    {
        return null;
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
                  notes = "Supply a Domain id, a Project id, a query string (query), a page number (pageNumber), and a page size(pageSize).  Returns a list of ObjectMetadata in a paginated structure",
                  responseContainer = "List",
                  response = ObjectMetadata.class)
    @Path("/search/{domainId}/{projectId}")
    @GET
    @Transactional
    @Timed
    public PaginatedResult<ObjectMetadata> search(@PathParam("domainId") String domainId,
                                                  @PathParam("projectId") String projectId,
                                                  @ApiParam(value = "The query string", required = true) @QueryParam("query") String query,
                                                  @ApiParam(value = "The page number for the results", required = true) @QueryParam("pageNumber") String pageNumber,
                                                  @ApiParam(value = "The page size for the results", required = true) @QueryParam("pageSize") String pageSize)
    {
        return null;
    }
}
