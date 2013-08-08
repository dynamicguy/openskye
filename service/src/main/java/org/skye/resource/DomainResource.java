package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import org.skye.domain.Domain;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.DomainDAO;

import javax.inject.Inject;
import javax.ws.rs.Path;
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


}
