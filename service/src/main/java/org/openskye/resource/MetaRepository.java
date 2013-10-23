package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.ArchiveStore;
import org.openskye.core.ObjectMetadata;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.stores.StoreRegistry;
import org.openskye.util.NotFoundException;
import org.openskye.util.UnauthorizedException;

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

                // Lets just pick up the first ACB containing the information
                if (metadata.getArchiveContentBlocks().size() > 0) {
                    ArchiveStoreDefinition archiveStoreDefinition = objectMetadataRepository.getArchiveStoreDefinition(metadata.getArchiveContentBlocks().get(0));
                    Optional<ArchiveStore> archiveStore = storeRegistry.build(archiveStoreDefinition);
                    if (archiveStore.isPresent()) {
                        Optional<InputStream> inputStream = archiveStore.get().getStream(metadata);
                        if (inputStream.isPresent()) {
                            return Response.ok(inputStream.get()).
                                    header("Content-Disposition", "attachment; filename=" + metadata.getPath()).build();
                        } else {
                            throw new NotFoundException();
                        }
                    } else throw new NotFoundException();
                }
            }
        } else {
            throw new UnauthorizedException();
        }
        throw new NotFoundException();
    }
}
