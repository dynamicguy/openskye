package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import org.skye.domain.AttributeInstance;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.AttributeInstanceDAO;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/attributeInstance", description = "Manage attributeInstances")
@Path("/api/1/attributeInstances")
@Produces(MediaType.APPLICATION_JSON)
public class AttributeInstanceResource extends AbstractUpdatableDomainResource<AttributeInstance> {

    @Inject
    protected AttributeInstanceDAO attributeInstanceDAO;

    @Override
    protected AbstractPaginatingDAO<AttributeInstance> getDAO() {
        return attributeInstanceDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "attributeInstance";
    }


}
