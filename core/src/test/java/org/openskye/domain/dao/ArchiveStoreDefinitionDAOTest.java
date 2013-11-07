package org.openskye.domain.dao;

import com.google.common.base.Optional;
import org.junit.Test;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.Domain;
import org.openskye.domain.Project;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Testing the {@link ArchiveStoreDefinitionDAO}
 */
public class ArchiveStoreDefinitionDAOTest extends AbstractDAOTestBase<ArchiveStoreDefinition> {

    @Inject
    public ArchiveStoreDefinitionDAO archiveStoreDefinitionDAO;

    @Override
    public AbstractPaginatingDAO getDAO() {
        return archiveStoreDefinitionDAO;
    }

    @Override
    public ArchiveStoreDefinition getNew() {
        Domain domain = new Domain();
        domain.setName("Fishstick");
        Project project = new Project();
        project.setName("Starship 1");
        project.setDomain(domain);
        ArchiveStoreDefinition isd = new ArchiveStoreDefinition();
        isd.setProject(project);
        isd.setName("Test Def");
        return isd;
    }

    @Override
    public void update(ArchiveStoreDefinition instance) {
        instance.setName("Test Def 2");
    }

    @Test(expected = ConstraintViolationException.class)
    public void tryMissingName() {
        ArchiveStoreDefinition isd = getNew();
        isd.setName(null);
        getDAO().create(isd);
    }

    @Test
    public void checkPropertiesPersist() {
        ArchiveStoreDefinition isd = new ArchiveStoreDefinition();
        isd.setName("Test Def");
        isd.getProperties().put("Hello", "world");
        getDAO().create(isd);

        Optional<ArchiveStoreDefinition> readBack = getDAO().get(isd.getId());
        assertThat(readBack.isPresent(), is(true));

        assertThat(readBack.get().getProperties().get("Hello"), is("world"));
    }
}
