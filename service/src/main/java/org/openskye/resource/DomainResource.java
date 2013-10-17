package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.Domain;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.DomainDAO;
import org.openskye.domain.dao.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link Domain}
 */
@Api(value = "/api/1/domains", description = "Manage domains")
@Path("/api/1/domains")
@Produces(MediaType.APPLICATION_JSON)
public class DomainResource extends AbstractUpdatableDomainResource<Domain> {

    private DomainDAO domainDAO;

    @Inject
    public DomainResource(DomainDAO dao) {
        this.domainDAO = dao;
    }

    @ApiOperation(value = "Create new domain", notes = "Create a new domain and return with its unique id", response = Domain.class)
    @POST
    @Transactional
    @Timed
    public Domain create(Domain newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update domain", notes = "Enter the id of the domain to update and the new information. Returns the updated domain", response = Domain.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public Domain update(@PathParam("id") String id, Domain newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find domain by id", notes = "Return a domain by its id", response = Domain.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public Domain get(@PathParam("id") String id) {
        return super.get(id);
    }

    @ApiOperation(value = "List all audit logs", notes = "Returns all audit logs in a paginated structure", responseContainer = "List", response = Domain.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<Domain> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete domain", notes = "Deletes the domain(found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
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
    public PaginatedResult<ArchiveStoreDefinition> getArchiveStores(@PathParam("id") String id) {
        Domain domain = get(id);
        return new PaginatedResult<ArchiveStoreDefinition>().paginate(domain.getArchiveStores());
    }

    @Path("/{id}/informationStores")
    @GET
    @ApiOperation(value = "Return the information stores owned by this domain")
    public PaginatedResult<InformationStoreDefinition> getInformationStores(@PathParam("id") String id) {

        Domain domain = get(id);
        return new PaginatedResult<InformationStoreDefinition>().paginate(domain.getInformationStores());
    }

}
