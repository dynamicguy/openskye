package org.openskye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.openskye.domain.Hold;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.HoldDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import static org.mockito.Mockito.mock;

/**
 * User: atcmostafavi Date: 1/9/14 Time: 4:18 PM Project: platform
 */
public class HoldResourceTest extends AbstractResourceTest<Hold> {
    public static final HoldDAO dao = mock(HoldDAO.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new HoldResource(dao))
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
            .build();
    private Hold hold = new Hold();
    private PaginatedResult<Hold> expectedResult = new PaginatedResult<>();

    @Override
    public String getSingular() {
        return "hold";
    }

    @Override
    public String getPlural() {
        return "holds";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public Hold getInstance() {
        return hold;
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
