package org.skye.resource;

import org.skye.domain.Domain;
import org.skye.resource.dao.AbstractPaginatingDAO;

import javax.ws.rs.Path;

/**
 * The REST endpoint for {@link Domain}
 */
@Path("/api/1/domains")
public class DomainResource extends AbstractDomainResource<Domain> {

    public DomainResource(AbstractPaginatingDAO<Domain> dao) {
        super(dao);
    }
}
