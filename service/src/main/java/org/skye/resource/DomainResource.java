package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.skye.domain.Domain;
import org.skye.domain.DomainArchiveStore;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.DomainDAO;
import org.skye.util.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for {@link Domain}
 */
@Api(value = "/api/1/domains", description = "Manage domains")
@Path("/api/1/domains")
@Produces(MediaType.APPLICATION_JSON)
public class DomainResource extends AbstractUpdatableDomainResource<Domain> {

    @Inject
    protected DomainDAO domainDAO;

    @Override
    protected AbstractPaginatingDAO<Domain> getDAO() {
        return domainDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "domain";
    }

    @Path("/{id}/archiveStores")
    @GET
    @ApiOperation(value = "Return the archive stores owned by this domain")
    public PaginatedResult<DomainArchiveStore> getArchiveStores(@PathParam("id") String id) {
        Domain domain = get(id);
        return new PaginatedResult<DomainArchiveStore>().paginate(domain.getArchiveStores());
    }

}
