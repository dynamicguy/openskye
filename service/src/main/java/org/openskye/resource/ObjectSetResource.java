package org.openskye.resource;

import com.wordnik.swagger.annotations.Api;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(value = "/api/1/objectSet", description = "Act upon ObjectMetadata using ObjectSet instances.")
@Path("/api/1/objectSet")
@Produces(MediaType.APPLICATION_JSON)
public class ObjectSetResource
{
}
