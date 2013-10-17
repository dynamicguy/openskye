package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.Api;
import org.openskye.core.SkyePlatform;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(value = "/api/1/platform", description = "Get platform details")
@Path("/api/1/platform")
@Produces(MediaType.APPLICATION_JSON)
/**
 * Resource to provide the current platform version information
 */
public class PlatformResource {

    @GET
    @Timed
    public SkyePlatform getPlatform() {
        return new SkyePlatform();
    }
}
