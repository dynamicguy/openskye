package org.skye.resource;

import com.google.common.base.Optional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import com.yammer.metrics.annotation.Timed;
import org.apache.shiro.SecurityUtils;
import org.skye.core.ArchiveContentBlock;
import org.skye.core.SimpleObject;
import org.skye.metadata.ObjectMetadataRepository;
import org.skye.util.UnauthorizedException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

    @ApiOperation(value = "Get simple object by id", notes = "Return an instance by id")
    @Path("/{id}")
    @GET
    @UnitOfWork
    @Timed
    public Optional<SimpleObject> get(@PathParam("id") String id) {
        if (SecurityUtils.getSubject().isPermitted("repository:get")) {
            return objectMetadataRepository.get(id);
        } else {
            throw new UnauthorizedException();
        }
    }

    @ApiOperation(value = "Get content blocks for simple object with id", notes = "Return content block meta data")
    @Path("/{id}/blocks")
    @GET
    @UnitOfWork
    @Timed
    public Iterable<ArchiveContentBlock> getContentBlocks(@PathParam("id") String id) {
        if (SecurityUtils.getSubject().isPermitted("repository:get")) {
            if (objectMetadataRepository.get(id).isPresent()) {
                return objectMetadataRepository.getArchiveContentBlocks(objectMetadataRepository.get(id).get());
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
    @UnitOfWork
    @Timed
    public Response getContent(@PathParam("id") String id) {
        if (SecurityUtils.getSubject().isPermitted("repository:get")) {
            Optional<SimpleObject> simpleObject = objectMetadataRepository.get(id);
            if (simpleObject.isPresent()) {
                return Response.ok(objectMetadataRepository.getContent(simpleObject.get())).header("Content-Disposition", "attachment; filename=" + simpleObject.get().getPath()).build();
            }
            ;
        } else {
            throw new UnauthorizedException();
        }
        return null;
    }
}
