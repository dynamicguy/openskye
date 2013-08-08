package org.skye.resource;

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
@Path("/api/1/domains")
@Produces(MediaType.APPLICATION_JSON)
public class DomainResource extends AbstractUpdatableDomainResource<Domain> {

    @Inject
    private DomainDAO domainDAO;

    @Override
    protected AbstractPaginatingDAO<Domain> getDAO() {
        return domainDAO;
    }
}
