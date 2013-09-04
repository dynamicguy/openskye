package org.skye.resource;

import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;
import org.skye.domain.Domain;
import org.skye.domain.DomainArchiveStore;
import org.skye.domain.DomainInformationStore;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.DomainDAO;
import org.skye.util.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.*;
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

    @ApiOperation(value = "Create new", notes = "Create a new instance and return with id", response = Domain.class)
    @POST
    @Transactional
    @Timed
    public Domain create(Domain newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update instance", notes = "Update the instance", response = Domain.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public Domain update(@PathParam("id") String id, Domain newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find by id", notes = "Return an instance by id", response = Domain.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public Domain get(@PathParam("id") String id) {
        return super.get(id);
    }

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

    @Path("/{id}/informationStores")
    @GET
    @ApiOperation(value = "Return the information stores owned by this domain")
    public PaginatedResult<DomainInformationStore> getInformationStores(@PathParam("id") String id) {

        Domain domain = get(id);
        return new PaginatedResult<DomainInformationStore>().paginate(domain.getInformationStores());
    }

}
