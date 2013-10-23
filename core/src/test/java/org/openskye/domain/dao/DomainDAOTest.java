package org.openskye.domain.dao;

import org.junit.Test;
import org.openskye.domain.Domain;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

/**
 * Testing the {@link DomainDAO}
 */
public class DomainDAOTest extends AbstractDAOTestBase<Domain> {

    @Inject
    public DomainDAO domainDAO;

    @Override
    public AbstractPaginatingDAO getDAO() {
        return domainDAO;
    }

    @Override
    public Domain getNew() {
        Domain domain = new Domain();
        domain.setName("Fishstick");
        return domain;
    }

    @Override
    public void update(Domain instance) {
        instance.setName("Fishstick2");
    }

    @Test(expected = ConstraintViolationException.class)
    public void tryMissingName() {
        Domain domain = getNew();
        domain.setName(null);
        getDAO().create(domain);
    }
}
