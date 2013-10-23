package org.openskye.domain.dao;

import com.google.common.base.Optional;
import org.junit.Test;
import org.openskye.domain.ArchiveStoreInstance;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Testing the {@link org.openskye.domain.dao.ArchiveStoreInstanceDAO}
 */
public class ArchiveStoreInstanceDAOTest extends AbstractDAOTestBase<ArchiveStoreInstance> {

    @Inject
    public ArchiveStoreInstanceDAO archiveStoreInstanceDAO;

    @Override
    public AbstractPaginatingDAO getDAO() {
        return archiveStoreInstanceDAO;
    }

    @Override
    public ArchiveStoreInstance getNew() {
        ArchiveStoreInstance isd = new ArchiveStoreInstance();
        isd.setImplementation("demo");
        isd.setName("Test Def");
        return isd;
    }

    @Override
    public void update(ArchiveStoreInstance instance) {
        instance.setName("Test Def 2");
    }

    @Test(expected = ConstraintViolationException.class)
    public void tryMissingName() {
        ArchiveStoreInstance isd = getNew();
        isd.setName(null);
        isd.setImplementation(null);
        getDAO().create(isd);
    }

    @Test
    public void checkPropertiesPersist() {
        ArchiveStoreInstance isd = new ArchiveStoreInstance();
        isd.setImplementation("demo");
        isd.setName("Test Def");
        isd.getProperties().put("Hello", "world");
        getDAO().create(isd);

        Optional<ArchiveStoreInstance> readBack = getDAO().get(isd.getId());
        assertThat(readBack.isPresent(), is(true));

        assertThat(readBack.get().getProperties().get("Hello"), is("world"));
    }
}
