package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.skye.core.SkyePlatform;
import org.springframework.stereotype.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Resource to provide the current platform version information
 */
@Api(value = "/api/1/platform", description = "Platform release information")
@Path("/api/1/platform")
@Produces(MediaType.APPLICATION_JSON)
@Service
public class PlatformResource {

    @GET
    @Timed
    @ApiOperation(value = "Get platform release information", notes = "An easy way to find the version and build id for the running instance", responseClass = "org.skye.core.SkyePlatform")
    public SkyePlatform getPlatform() {
        return new SkyePlatform();
    }
}
