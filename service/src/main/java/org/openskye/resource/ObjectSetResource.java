package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.wordnik.swagger.annotations.ApiOperation;

import com.wordnik.swagger.annotations.ApiParam;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.ObjectSet;
import org.openskye.domain.dao.PaginatedResult;

@Api(value = "/api/1/objectSet", description = "Act upon ObjectMetadata using ObjectSet instances.")
@Path("/api/1/objectSet")
@Produces(MediaType.APPLICATION_JSON)
public class ObjectSetResource
{
    @Inject
    private ObjectMetadataRepository repository;

    @ApiOperation(value = "Creates an ObjectSet",
                  notes = "Supply the name of the ObjectSet to be created.  " +
                          "Returns the created ObjectSet with its new id.",
                  response = ObjectSet.class)
    @POST
    @Transactional
    @Timed
    public ObjectSet create(@ApiParam(value = "The name of the ObjectSet", required = true)
                            @QueryParam("name")
                            String objectSetName)
    {
        return null;
    }

    @ApiOperation(value = "Gets an ObjectSet",
                  notes = "Supply the id of the ObjectSet.  Returns the requested ObjectSet",
                  response = ObjectSet.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    public ObjectSet get(@PathParam("id") String id)
    {
        return null;
    }

    @ApiOperation(value = "Gets all ObjectSet instances",
                  notes = "Returns all ObjectSet instances in a paginated structure",
                  responseContainer = "List",
                  response = ObjectSet.class)
    @GET
    @Transactional
    @Timed
    public PaginatedResult<ObjectSet> getAll()
    {
        return null;
    }

    @ApiOperation(value = "Deletes an ObjectSet",
                  notes = "Supply the id of the ObjectSet to be deleted")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    public Response delete(@PathParam("id") String id)
    {
        return null;
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
                           String metadataId)
    {
        return false;
    }

    @ApiOperation(value = "Gets all ObjectMetadata in a given ObjectSet",
                  notes = "Supply the ObjectSet id.  Returns a paginated structure containing all ObjectMetadata in the set",
                  responseContainer = "List",
                  response = ObjectMetadata.class)
    @Path("/metadata/{id}")
    @GET
    @Transactional
    @Timed
    public PaginatedResult<ObjectMetadata> getObjects(@PathParam("id") String id)
    {
        return null;
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
                              String metadataId)
    {
        return null;
    }

    @ApiOperation(value = "Deletes a referece for an ObjectMetadata from the ObjectSet",
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
                                 String metadataId)
    {
        return null;
    }
}

