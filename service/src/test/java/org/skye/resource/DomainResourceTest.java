package org.skye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.skye.domain.Domain;
import org.skye.domain.dao.AbstractPaginatingDAO;
import org.skye.domain.dao.DomainDAO;
import org.skye.domain.dao.PaginatedResult;

import static org.mockito.Mockito.mock;

public class DomainResourceTest extends AbstractResourceTest<Domain> {
    private static final DomainDAO dao = mock(DomainDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new DomainResource(dao))
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
    public Object getExpectedResult() {
        return expectedResult;
    }
}