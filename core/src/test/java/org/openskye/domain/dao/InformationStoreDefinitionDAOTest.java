package org.openskye.domain.dao;

import com.google.common.base.Optional;
import org.junit.Test;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.Domain;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.Project;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Testing the {@link org.openskye.domain.dao.InformationStoreDefinitionDAO}
 */
public class InformationStoreDefinitionDAOTest extends AbstractDAOTestBase<InformationStoreDefinition> {

    @Inject
    public InformationStoreDefinitionDAO informationStoreDefinitionDAO;

    @Override
    public AbstractPaginatingDAO getDAO() {
        return informationStoreDefinitionDAO;
    }

    @Override
    public InformationStoreDefinition getNew() {
        Domain domain = new Domain();
        domain.setName("Fishstick");
        Project project = new Project();
        project.setName("Starship 1");
        project.setDomain(domain);
        InformationStoreDefinition isd = new InformationStoreDefinition();
        isd.setProject(project);
        isd.setName("Test Def");
        return isd;
    }

    @Override
    public void update(InformationStoreDefinition instance) {
        instance.setName("Test Def 2");
    }

    @Test(expected = ConstraintViolationException.class)
    public void tryMissingName() {
        InformationStoreDefinition isd = getNew();
        isd.setName(null);
        getDAO().create(isd);
    }

    @Test
    public void checkPropertiesPersist() {
        InformationStoreDefinition isd = new InformationStoreDefinition();
        isd.setName("Test Def");
        isd.getProperties().put("Hello", "world");
        getDAO().create(isd);

        Optional<InformationStoreDefinition> readBack = getDAO().get(isd.getId());
        assertThat(readBack.isPresent(), is(true));

        assertThat(readBack.get().getProperties().get("Hello"), is("world"));
    }
}
