package org.openskye.domain.dao;

import org.junit.Test;
import org.openskye.domain.Domain;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.Project;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

/**
 * Testing the {@link org.openskye.domain.dao.InformationStoreDefinitionDAO}
 */
public class InformationStoreInstanceDAOTest extends AbstractDAOTestBase<InformationStoreDefinition> {

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
}
