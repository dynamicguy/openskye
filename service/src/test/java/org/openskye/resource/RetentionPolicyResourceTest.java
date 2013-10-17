package org.openskye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.openskye.domain.RetentionPolicy;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.domain.dao.RetentionPolicyDAO;

import static org.mockito.Mockito.mock;

public class RetentionPolicyResourceTest extends AbstractResourceTest<RetentionPolicy> {
    private static final RetentionPolicyDAO dao = mock(RetentionPolicyDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new RetentionPolicyResource(dao))
            .build();
    private final RetentionPolicy retentionPolicy = new RetentionPolicy();
    private PaginatedResult<RetentionPolicy> expectedResult = new PaginatedResult<>();

    @Override
    public String getSingular() {
        return "retentionPolicy";
    }

    @Override
    public String getPlural() {
        return "retentionPolicies";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public RetentionPolicy getInstance() {
        return retentionPolicy;
    }

    @Override
    public AbstractPaginatingDAO getDAO() {
        return dao;
    }

    @Override
    public PaginatedResult getExpectedResult() {
        return expectedResult;
    }
}