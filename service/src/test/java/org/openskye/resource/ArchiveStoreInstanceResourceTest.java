package org.openskye.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.ArchiveStoreInstance;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.ArchiveStoreInstanceDAO;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.exceptions.AuthenticationExceptionMapper;
import org.openskye.exceptions.AuthorizationExceptionMapper;

import static org.mockito.Mockito.mock;

public class ArchiveStoreInstanceResourceTest extends AbstractResourceTest<ArchiveStoreInstance> {

    public static ArchiveStoreInstanceDAO dao=mock(ArchiveStoreInstanceDAO.class);

    @ClassRule
    public static final ResourceTestRule resources=ResourceTestRule.builder()
            .addResource(new ArchiveStoreInstanceResource(dao))
            .addProvider(new AuthorizationExceptionMapper())
            .addProvider(new AuthenticationExceptionMapper())
            .build();

    private final ArchiveStoreInstance archiveStoreInstance=new ArchiveStoreInstance();
    private PaginatedResult<ArchiveStoreInstance> expectedResult=new PaginatedResult<>();

    @Override
    public String getSingular() {
        return "archiveStoreInstance";
    }

    @Override
    public String getPlural() {
        return "archiveStoreInstances";
    }

    @Override
    public ResourceTestRule getResources() {
        return resources;
    }

    @Override
    public ArchiveStoreInstance getInstance() {
        return archiveStoreInstance;
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
