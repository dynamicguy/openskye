package org.skye.resource;

import com.wordnik.swagger.annotations.Api;
import org.skye.domain.RetentionPolicy;
import org.skye.resource.dao.AbstractPaginatingDAO;
import org.skye.resource.dao.RetentionPolicyDAO;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The REST endpoint for {@link org.skye.domain.Domain}
 */
@Api(value = "/api/1/retentionPolicies", description = "Manage retentionPolicies")
@Path("/api/1/retentionPolicies")
@Produces(MediaType.APPLICATION_JSON)
public class RetentionPolicyResource extends AbstractUpdatableDomainResource<RetentionPolicy> {

    @Inject
    protected RetentionPolicyDAO retentionPolicyDAO;

    @Override
    protected AbstractPaginatingDAO<RetentionPolicy> getDAO() {
        return retentionPolicyDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "retentionPolicy";
    }


}
