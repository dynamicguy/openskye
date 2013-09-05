package org.skye.resource;

import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.skye.domain.MetadataTemplate;
import org.skye.domain.Permission;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.MetadataTemplateDAO;
import org.skye.util.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/metadataTemplates", description = "Manage metadataTemplates")
@Path("/api/1/metadataTemplates")
@Produces(MediaType.APPLICATION_JSON)
public class MetadataTemplateResource extends AbstractUpdatableDomainResource<MetadataTemplate> {

    @Inject
    protected MetadataTemplateDAO metadataTemplateDAO;

    @ApiOperation(value = "Create new metadata template", notes = "Create a new metadata template and return with its unique id", response = MetadataTemplate.class)
    @POST
    @Transactional
    @Timed
    public MetadataTemplate create(MetadataTemplate newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update metadata template", notes = "Enter the id of the metadata template to update, returns updated metadata template", response = MetadataTemplate.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public MetadataTemplate update(@PathParam("id") String id, MetadataTemplate newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find metadata template by id", notes = "Return a metadata template by its id", response = MetadataTemplate.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public MetadataTemplate get(@PathParam("id") String id) {
        return super.get(id);
    }

    @Override
    protected AbstractPaginatingDAO<MetadataTemplate> getDAO() {
        return metadataTemplateDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "metadataTemplate";
    }

    @Path("/{id}/archiveStores")
    @GET
    @ApiOperation(value = "Return the archive stores owned by this domain")
    public PaginatedResult<Permission> getFieldDefinitions(@PathParam("id") String id) {
        MetadataTemplate metadataTemplate = get(id);
        return new PaginatedResult<Permission>().paginate(metadataTemplate.getFieldDefinitions());

    }


}
