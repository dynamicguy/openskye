package org.openskye.domain.dao;

import com.google.inject.Inject;
import org.openskye.domain.PeriodType;
import org.openskye.domain.RetentionPolicy;

/**
 * Test the RetentionPolicyDAO
 */
public class RetentionPolicyDAOTest extends AbstractDAOTestBase<RetentionPolicy> {

    @Inject
    public RetentionPolicyDAO retentionPolicyDAO;

    @Override
    public AbstractPaginatingDAO<RetentionPolicy> getDAO() {
        return retentionPolicyDAO;
    }

    @Override
    public RetentionPolicy getNew() {
        RetentionPolicy retentionPolicy = new RetentionPolicy();
        retentionPolicy.setDescription("Test def ");
        retentionPolicy.setName("Philip Dodds");
        retentionPolicy.setRecordsCode("Records code");
        retentionPolicy.setPeriodType(PeriodType.DAY);
        return retentionPolicy;
    }

    @Override
    public void update(RetentionPolicy instance) {
        instance.setDescription("Test Def");
        instance.setName("Philip Dodds");
        instance.setRecordsCode("Records code");
    }
}
