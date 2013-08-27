package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import org.skye.domain.AttributeDefinition;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.AttributeDefinitionDAO;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/attributeDefinitions", description = "Manage attributeDefinitions")
@Path("/api/1/attributeDefinitions")
@Produces(MediaType.APPLICATION_JSON)
public class AttributeDefinitionResource extends AbstractUpdatableDomainResource<AttributeDefinition> {

    @Inject
    protected AttributeDefinitionDAO attributeDefinitionDAO;

    @Override
    protected AbstractPaginatingDAO<AttributeDefinition> getDAO() {
        return attributeDefinitionDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "attributeDefinition";
    }


}
