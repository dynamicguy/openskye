package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.skye.domain.MetadataTemplate;
import org.skye.domain.Permission;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.MetadataTemplateDAO;
import org.skye.util.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
