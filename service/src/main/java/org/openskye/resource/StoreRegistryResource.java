package org.openskye.resource;

import com.wordnik.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.openskye.stores.StoreRegistry;
import org.openskye.stores.StoreRegistryMetadata;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for accessing the meta-data for the available archive and information
 * stores
 */
@Api(value = "/api/1/platform/registry", description = "See what archive and information store implementations have been registered on this server")
@Path("/api/1/platform/registry")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class StoreRegistryResource {

    @Inject
    private StoreRegistry storeRegistry;

    @GET
    public StoreRegistryMetadata getMetadata() {
        log.debug("Request for store registry metadata on " + storeRegistry.getMetadata());
        return storeRegistry.getMetadata();
    }
}
