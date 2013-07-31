package org.skye.resource;

import com.yammer.metrics.annotation.Timed;
import org.skye.core.SkyePlatform;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Resource to provide the current platform version information
 */
@Path("/platform")
@Produces(MediaType.APPLICATION_JSON)
public class PlatformResource {

    @GET
    @Timed
    public SkyePlatform getPlatform() {
        return new SkyePlatform();
    }
}
