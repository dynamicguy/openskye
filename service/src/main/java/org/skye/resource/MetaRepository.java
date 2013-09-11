package org.skye.resource;

import com.google.common.base.Optional;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.apache.shiro.SecurityUtils;
import org.skye.core.ArchiveContentBlock;
import org.skye.core.ArchiveStore;
import org.skye.core.ObjectMetadata;
import org.skye.metadata.ObjectMetadataRepository;
import org.skye.stores.StoreRegistry;
import org.skye.util.NotFoundException;
import org.skye.util.UnauthorizedException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * Search and access information from the {@link ObjectMetadataRepository}
 */
@Api(value = "/api/1/repository", description = "Access the meta repository")
@Path("/api/1/repository")
@Produces(MediaType.APPLICATION_JSON)
/**
 * Manage domains
 */
public class MetaRepository {

    @Inject
    protected ObjectMetadataRepository objectMetadataRepository;
    @Inject
    private StoreRegistry storeRegistry;

    @ApiOperation(value = "Get simple object by id", notes = "Return an instance by id")
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    public Optional<ObjectMetadata> get(@PathParam("id") String id) {
        if (SecurityUtils.getSubject().isPermitted("repository:get")) {
            return objectMetadataRepository.get(id);
        } else {
            throw new UnauthorizedException();
        }
    }

    @ApiOperation(value = "Get content blocks for simple object with id", notes = "Return content block meta data")
    @Path("/{id}/blocks")
    @GET
    @Transactional
    @Timed
    public Iterable<ArchiveContentBlock> getContentBlocks(@PathParam("id") String id) {
        if (SecurityUtils.getSubject().isPermitted("repository:get")) {
            if (objectMetadataRepository.get(id).isPresent()) {
                return objectMetadataRepository.get(id).get().getArchiveContentBlocks();
            }
            ;
        } else {
            throw new UnauthorizedException();
        }
        return null;
    }

    @ApiOperation(value = "Get the raw content of the simple object", notes = "Return raw content of the simple object")
    @Path("/{id}/content")
    @GET
    @Transactional
    @Timed
    public Response getContent(@PathParam("id") String id) {
        if (SecurityUtils.getSubject().isPermitted("repository:get")) {
            Optional<ObjectMetadata> objectMetadata = objectMetadataRepository.get(id);
            if (objectMetadata.isPresent()) {
                ObjectMetadata metadata = objectMetadata.get();
                Optional<ArchiveStore> archiveStore = storeRegistry.build(metadata.getArchiveStoreDefinition());
                if (archiveStore.isPresent()) {
                    InputStream inputStream = archiveStore.get().getStream(metadata);
                    return Response.ok(inputStream).
                            header("Content-Disposition", "attachment; filename=" + metadata.getPath()).build();
                }
            }
        } else {
            throw new UnauthorizedException();
        }
        throw new NotFoundException();
    }
}
