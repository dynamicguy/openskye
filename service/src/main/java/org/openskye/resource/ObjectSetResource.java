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
import org.openskye.domain.Domain;
import org.openskye.domain.dao.DomainDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.NotFoundException;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    private DomainDAO domains;

    @Inject
    public ObjectSetResource(ObjectMetadataRepository injectedRepository, ObjectMetadataSearch injectedSearch) {
        repository = injectedRepository;
        search = injectedSearch;
    }

    @Inject
    public ObjectSetResource setDomainDAO(DomainDAO injectedDao) {
        domains = injectedDao;

        return this;
    }

    @ApiOperation(value = "Creates an ObjectSet",
            notes = "Supply the name of the ObjectSet to be created.  " +
                    "Returns the created ObjectSet with its new id.",
            response = ObjectSet.class)
    @POST
    @Transactional
    @Timed
    public ObjectSet create(ObjectSet newInstance) {
        if (!this.isPermitted(OPERATION_CREATE))
            throw new UnauthorizedException();

        return this.repository.createObjectSet(newInstance.getName());
    }

    @ApiOperation(value = "Gets an ObjectSet",
            notes = "Supply the id of the ObjectSet.  Returns the requested ObjectSet",
            response = ObjectSet.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    public ObjectSet get(@PathParam("id") String id) {
        Optional<ObjectSet> objectSet;

        if (!this.isPermitted(OPERATION_GET))
            throw new UnauthorizedException();

        objectSet = this.repository.getObjectSet(id);

        if (!objectSet.isPresent())
            throw new NotFoundException();

        return objectSet.get();
    }

    @ApiOperation(value = "Gets all ObjectSet instances",
            notes = "Returns all ObjectSet instances in a paginated structure",
            responseContainer = "List",
            response = ObjectSet.class)
    @GET
    @Transactional
    @Timed
    public PaginatedResult<ObjectSet> getAll() {
        if (!this.isPermitted(OPERATION_LIST))
            throw new UnauthorizedException();

        PaginatedResult<ObjectSet> result = new PaginatedResult<>(this.repository.getAllObjectSets());

        return result;
    }

    @ApiOperation(value = "Deletes an ObjectSet",
            notes = "Supply the id of the ObjectSet to be deleted")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    public Response delete(@PathParam("id") String id) {
        Optional<ObjectSet> objectSet;

        if (!this.isPermitted(OPERATION_DELETE))
            throw new UnauthorizedException();

        objectSet = this.repository.getObjectSet(id);

        if (!objectSet.isPresent())
            throw new NotFoundException();

        this.repository.deleteObjectSet(objectSet.get());

        return Response.ok().build();
    }

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
        Optional<ObjectSet> objectSet;
        Optional<ObjectMetadata> objectMetadata;

        if (!this.isPermitted(OPERATION_GET))
            throw new UnauthorizedException();

        objectSet = this.repository.getObjectSet(setId);

        if (!objectSet.isPresent())
            throw new NotFoundException();

        objectMetadata = this.repository.get(metadataId);

        if (!objectMetadata.isPresent())
            throw new NotFoundException();

        return this.repository.isObjectInSet(objectSet.get(), objectMetadata.get());
    }

    @ApiOperation(value = "Gets all ObjectMetadata in a given ObjectSet",
            notes = "Supply the ObjectSet id.  Returns a paginated structure containing all ObjectMetadata in the set",
            responseContainer = "List",
            response = ObjectMetadata.class)
    @Path("/metadata/{id}")
    @GET
    @Transactional
    @Timed
    public PaginatedResult<ObjectMetadata> getObjects(@PathParam("id") String id) {
        PaginatedResult<ObjectMetadata> result;
        Optional<ObjectSet> objectSet;

        if (!this.isPermitted(OPERATION_GET))
            throw new UnauthorizedException();

        objectSet = this.repository.getObjectSet(id);

        if (!objectSet.isPresent())
            throw new NotFoundException();

        result = new PaginatedResult<>(this.repository.getObjects(objectSet.get()));

        return result;
    }

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
        Optional<ObjectSet> objectSet;
        Optional<ObjectMetadata> objectMetadata;

        if (!this.isPermitted(OPERATION_ADD))
            throw new UnauthorizedException();

        objectSet = this.repository.getObjectSet(setId);

        if (!objectSet.isPresent())
            throw new NotFoundException();

        objectMetadata = this.repository.get(metadataId);

        if (!objectMetadata.isPresent())
            throw new NotFoundException();

        this.repository.addObjectToSet(objectSet.get(), objectMetadata.get());

        return Response.ok().build();
    }

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
        Optional<ObjectSet> objectSet;
        Optional<ObjectMetadata> objectMetadata;

        if (!this.isPermitted(OPERATION_REMOVE))
            throw new UnauthorizedException();

        objectSet = this.repository.getObjectSet(setId);

        if (!objectSet.isPresent())
            throw new NotFoundException();

        objectMetadata = this.repository.get(metadataId);

        if (!objectMetadata.isPresent())
            throw new NotFoundException();

        this.repository.removeObjectFromSet(objectSet.get(), objectMetadata.get());

        return Response.ok().build();
    }

    @ApiOperation(value = "Add the results of a search to an ObjectSet",
            notes = "Supply the ObjectSet id and the Domain id, as well as a query string. " +
                    "Returns the ObjectSet with which the results can be accessed",
            response = ObjectSet.class)
    @Path("/{setId}/search/{domainId}")
    @PUT
    @Transactional
    @Timed
    public ObjectSet addFromSearch(@ApiParam(value = "The id of the ObjectSet", required = true)
                                   @PathParam("setId")
                                   String setId,
                                   @ApiParam(value = "The id of the Domain to be searched", required = true)
                                   @PathParam("domainId")
                                   String domainId,
                                   @ApiParam(value = "The query string to perform", required = true)
                                   @QueryParam("query")
                                   String query) {
        Optional<ObjectSet> objectSet;
        Optional<Domain> domain;
        Iterable<ObjectMetadata> metadataList;

        if (!this.isPermitted(OPERATION_SEARCH))
            throw new UnauthorizedException();

        objectSet = this.repository.getObjectSet(setId);

        if (!objectSet.isPresent())
            throw new NotFoundException();

        domain = this.domains.get(domainId);

        if (!domain.isPresent())
            throw new NotFoundException();

        metadataList = this.search.search(domain.get(), query);

        for (ObjectMetadata metadata : metadataList)
            this.repository.addObjectToSet(objectSet.get(), metadata);

        return objectSet.get();
    }

    private boolean isPermitted(String operation) {
        return SecurityUtils.getSubject().isPermitted(operation);
    }
}

