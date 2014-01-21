package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.ObjectSet;
import org.openskye.core.SearchPage;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.NotFoundException;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Resource which operates on {@link ObjectSet} instances.
 */
@Api(value = "/api/1/objectSets", description = "Act upon ObjectMetadata using ObjectSet instances.")
@Path("/api/1/objectSets")
@Produces(MediaType.APPLICATION_JSON)
public class ObjectSetResource {
    public static final String OPERATION_CREATE = "objectSets:create";
    public static final String OPERATION_GET = "objectSets:get";
    public static final String OPERATION_LIST = "objectSets:list";
    public static final String OPERATION_ADD = "objectSets:add";
    public static final String OPERATION_REMOVE = "objectSets:remove";
    public static final String OPERATION_DELETE = "objectSets:delete";
    public static final String OPERATION_SEARCH = "objectSets:search";
    private ObjectMetadataRepository repository;
    private ObjectMetadataSearch search;

    /**
     * Constructs a working ObjectSetResource.
     *
     * @param injectedRepository The {@link ObjectMetadataRepository} for the resource.
     * @param injectedSearch     The {@link ObjectMetadataSearch} for the resource.
     */
    @Inject
    public ObjectSetResource(ObjectMetadataRepository injectedRepository, ObjectMetadataSearch injectedSearch) {
        repository = injectedRepository;
        search = injectedSearch;
    }

    /**
     * Creates a new {@link ObjectSet}.
     *
     * @param newInstance The new instance to be created.
     * @return The newly created instance, with its id field set.
     */
    @ApiOperation(value = "Creates an ObjectSet",
            notes = "Supply the name of the ObjectSet to be created.  " +
                    "Returns the created ObjectSet with its new id.",
            response = ObjectSet.class)
    @POST
    @Transactional
    @Timed
    public ObjectSet create(ObjectSet newInstance) {
        checkPermission(OPERATION_CREATE);

        return repository.createObjectSet(newInstance.getName());
    }

    /**
     * Holds the requested {@link ObjectSet}.
     *
     * @param id The id of the {@link ObjectSet}.
     * @return The updated {@link ObjectSet}.
     */
    @ApiOperation(value = "Holds an ObjectSet",
            notes = "Supply the id of the ObjectSet.  Holds the requested ObjectSet",
            response = ObjectSet.class)
    @Path("/{id}/hold")
    @PUT
    @Transactional
    @Timed
    public ObjectSet hold(@PathParam("id") String id) {

        checkPermission(OPERATION_GET);

        Optional<ObjectSet> objectSet = repository.getObjectSet(id);

        if (!objectSet.isPresent())
            throw new NotFoundException();

        objectSet.get().setOnHold(true);
        
        repository.updateObjectSet(objectSet);

        return objectSet.get();
    }

    /**
     * Unholds the requested {@link ObjectSet}.
     *
     * @param id The id of the {@link ObjectSet}.
     * @return The updated {@link ObjectSet}.
     */
    @ApiOperation(value = "Unholds an ObjectSet",
            notes = "Supply the id of the ObjectSet.  Unholds the requested ObjectSet",
            response = ObjectSet.class)
    @Path("/{id}/unhold")
    @PUT
    @Transactional
    @Timed
    public ObjectSet unhold(@PathParam("id") String id) {

        checkPermission(OPERATION_GET);

        Optional<ObjectSet> objectSet = repository.getObjectSet(id);

        if (!objectSet.isPresent())
            throw new NotFoundException();

        objectSet.get().setOnHold(false);
        repository.updateObjectSet(objectSet);

        return objectSet.get();
    }

    /**
     * Gets the requested {@link ObjectSet}.
     *
     * @param id The id of the {@link ObjectSet}.
     * @return The requested {@link ObjectSet}.
     */
    @ApiOperation(value = "Gets an ObjectSet",
            notes = "Supply the id of the ObjectSet.  Returns the requested ObjectSet",
            response = ObjectSet.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    public ObjectSet get(@PathParam("id") String id) {

        checkPermission(OPERATION_GET);

        Optional<ObjectSet> objectSet = repository.getObjectSet(id);

        if (!objectSet.isPresent())
            throw new NotFoundException();

        return objectSet.get();
    }

    /**
     * Lists all existing {@link ObjectSet} instances.
     *
     * @return A {@link PaginatedResult} structure listing all {@link ObjectSet} instances.
     */
    @ApiOperation(value = "Gets all ObjectSet instances",
            notes = "Returns all ObjectSet instances in a paginated structure",
            responseContainer = "List",
            response = ObjectSet.class)
    @GET
    @Transactional
    @Timed
    public PaginatedResult<ObjectSet> getAll() {
        checkPermission(OPERATION_LIST);

        return new PaginatedResult<>(repository.getAllObjectSets());
    }

    /**
     * Deletes the requested {@link ObjectSet}.
     *
     * @param id The id of the {@link ObjectSet} to be deleted.
     * @return A {@link Response} which indicates that the set was deleted.
     */
    @ApiOperation(value = "Deletes an ObjectSet",
            notes = "Supply the id of the ObjectSet to be deleted")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    public Response delete(@PathParam("id") String id) {

        checkPermission(OPERATION_DELETE);

        Optional<ObjectSet> objectSet = repository.getObjectSet(id);

        if (!objectSet.isPresent())
            throw new NotFoundException();

        repository.deleteObjectSet(objectSet.get());

        return Response.ok().build();
    }

    /**
     * Checks to see if the specified {@link ObjectMetadata} is found in the {@link ObjectSet}.
     *
     * @param setId      The id of the {@link ObjectSet} to be queried.
     * @param metadataId The id of the {@link ObjectMetadata} to be found.
     * @return True if the {@link ObjectMetadata} is found in the {@link ObjectSet}, or false if it is not.
     */
    @ApiOperation(value = "Determines if the ObjectMetadata is found in the ObjectSet",
            notes = "Supply the id of the ObjectSet and the id of the ObjectMetadata.  " +
                    "Returns true if the ObjectMetadata is in the set, or false if it is not",
            response = Boolean.class)
    @Path("/{setId}/contains/{metadataId}")
    @GET
    @Transactional
    @Timed
    public boolean isFound(@ApiParam(value = "The id of the ObjectSet", required = true)
                           @PathParam("setId")
                           String setId,
                           @ApiParam(value = "The id of the ObjectMetadata", required = true)
                           @PathParam("metadataId")
                           String metadataId) {

        checkPermission(OPERATION_GET);

        Optional<ObjectSet> objectSet = repository.getObjectSet(setId);

        if (!objectSet.isPresent())
            throw new NotFoundException();

        Optional<ObjectMetadata> objectMetadata = repository.get(metadataId);

        if (!objectMetadata.isPresent())
            throw new NotFoundException();

        return repository.isObjectInSet(objectSet.get(), objectMetadata.get());
    }

    /**
     * Gets all {@link ObjectMetadata} instances in the requested {@link ObjectSet}.
     *
     * @param id The id of the {@link ObjectSet}.
     * @return A {@link PaginatedResult} structure containing a list of all {@link ObjectMetadata} instances found in the {@link ObjectSet}.
     */
    @ApiOperation(value = "Gets all ObjectMetadata in a given ObjectSet",
            notes = "Supply the ObjectSet id.  Returns a paginated structure containing all ObjectMetadata in the set",
            responseContainer = "List",
            response = ObjectMetadata.class)
    @Path("/{id}/metadata")
    @GET
    @Transactional
    @Timed
    public PaginatedResult<ObjectMetadata> getObjects(@PathParam("id") String id) {

        checkPermission(OPERATION_GET);

        Optional<ObjectSet> objectSet = repository.getObjectSet(id);

        if (!objectSet.isPresent())
            throw new NotFoundException();

        return new PaginatedResult<>(repository.getObjects(objectSet.get()));
    }

    /**
     * Adds an {@link ObjectMetadata} instance to the {@link ObjectSet}.
     *
     * @param setId      The id of a previously created {@link ObjectSet}.
     * @param metadataId The id of a previously created {@link ObjectMetadata}.
     * @return A {@link Response} which indicates that the {@link ObjectMetadata} was successfully added to the {@link ObjectSet}.
     */
    @ApiOperation(value = "Adds a reference for an ObjectMetadata to the ObjectSet",
            notes = "Supply the ObjectSet id and the ObjectMetadata id.  " +
                    "The ObjectMetadata and ObjectSet must have been previously created")
    @Path("/{setId}/{metadataId}")
    @PUT
    @Transactional
    @Timed
    public Response addObject(@ApiParam(value = "The id of the ObjectSet", required = true)
                              @PathParam("setId")
                              String setId,
                              @ApiParam(value = "The id of the ObjectMetadata to be added", required = true)
                              @PathParam("metadataId")
                              String metadataId) {

        checkPermission(OPERATION_ADD);

        Optional<ObjectSet> objectSet = repository.getObjectSet(setId);

        if (!objectSet.isPresent())
            throw new NotFoundException();

        Optional<ObjectMetadata> objectMetadata = repository.get(metadataId);

        if (!objectMetadata.isPresent())
            throw new NotFoundException();

        repository.addObjectToSet(objectSet.get(), objectMetadata.get());

        return Response.ok().build();
    }

    /**
     * Removes an {@link ObjectMetadata} instance from the {@link ObjectSet}.
     *
     * @param setId      The id of a previously created {@link ObjectSet}.
     * @param metadataId The id of a previously created {@link ObjectMetadata}.
     * @return A {@link Response} which indicates that the {@link ObjectMetadata} was successfully removed from the {@link ObjectSet}.
     */
    @ApiOperation(value = "Deletes a reference for an ObjectMetadata from the ObjectSet",
            notes = "Supply the ObjectSet id and the ObjectMetadata id.  " +
                    "The ObjectMetadata and ObjectSet must have been previously created.  " +
                    "This does not delete either record from the repository; " + "" +
                    "it simply removes the reference from the set")
    @Path("/{setId}/{metadataId}")
    @DELETE
    @Transactional
    @Timed
    public Response removeObject(@ApiParam(value = "The id of the ObjectSet", required = true)
                                 @PathParam("setId")
                                 String setId,
                                 @ApiParam(value = "The id of the ObjectMetadata to be removed", required = true)
                                 @PathParam("metadataId")
                                 String metadataId) {

        checkPermission(OPERATION_REMOVE);

        Optional<ObjectSet> objectSet = repository.getObjectSet(setId);

        if (!objectSet.isPresent())
            throw new NotFoundException();

        Optional<ObjectMetadata> objectMetadata = repository.get(metadataId);

        if (!objectMetadata.isPresent())
            throw new NotFoundException();

        repository.removeObjectFromSet(objectSet.get(), objectMetadata.get());

        return Response.ok().build();
    }

    /**
     * Searches for {@link ObjectMetadata} instances which match a query and adds them to the {@link ObjectSet}.
     *
     * @param setId The id of the {@link ObjectSet} to which the results will be added.
     * @param query The query string for which the search is run.
     * @return The {@link ObjectSet} to which the results are added.
     */
    @ApiOperation(value = "Add the results of a search to an ObjectSet",
            notes = "Supply the ObjectSet id as well as a query string. " +
                    "Returns the ObjectSet with which the results can be accessed",
            response = ObjectSet.class)
    @Path("/{setId}/search")
    @PUT
    @Transactional
    @Timed
    public ObjectSet addFromSearch(@ApiParam(value = "The id of the ObjectSet", required = true)
                                   @PathParam("setId")
                                   String setId,
                                   @ApiParam(value = "The query string to perform", required = true)
                                   @QueryParam("query")
                                   String query) {

        checkPermission(OPERATION_SEARCH);

        Optional<ObjectSet> objectSet = repository.getObjectSet(setId);

        if (!objectSet.isPresent())
            throw new NotFoundException();

        Iterable<ObjectMetadata> metadataList = search.search(query);

        for (ObjectMetadata metadata : metadataList)
            repository.addObjectToSet(objectSet.get(), metadata);

        return objectSet.get();
    }

    protected void checkPermission(String operation) {
        if (!SecurityUtils.getSubject().isPermitted(operation))
            throw new UnauthorizedException();
    }
}

