package org.openskye.domain.dao;

import org.junit.Test;
import org.openskye.domain.Hold;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

/**
 * Testing the {@link HoldDAO}
 */
public class HoldDAOTest extends AbstractDAOTestBase<Hold> {

    @Inject
    public HoldDAO holdDAO;

    @Override
    public AbstractPaginatingDAO getDAO() {
        return holdDAO;
    }

    @Override
    public Hold getNew() {
        Hold hold = new Hold();
        hold.setQuery("type:value");
        hold.setName("Test Hold");
        hold.setDescription("A test...");
        return hold;
    }

    @Override
    public void update(Hold instance) {
        instance.setName("Starship 2");
    }

    @Test(expected = ConstraintViolationException.class)
    public void tryMissingQuery() {
        Hold hold = getNew();
        hold.setQuery(null);
        holdDAO.create(hold);
    }
}
