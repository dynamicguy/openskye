package org.openskye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.openskye.domain.Domain;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.DomainDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import static org.mockito.Mockito.mock;

public class DomainResourceTest extends AbstractResourceTest<Domain> {

    public static final DomainDAO dao = mock(DomainDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new DomainResource(dao))
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
            .build();
    private final Domain domain = new Domain();
    private PaginatedResult<Domain> expectedResult = new PaginatedResult<>();

    @Override
    public String getSingular() {
        return "domain";
    }

    @Override
    public String getPlural() {
        return "domains";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public Domain getInstance() {
        return domain;
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