package org.openskye.domain.dao;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.junit.Test;
import org.openskye.domain.AuditEvent;
import org.openskye.domain.AuditLog;
import org.openskye.domain.AuditLogProperty;

import javax.validation.ConstraintViolationException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 Test the AuditLogPropertyDAO
 */
public class AuditLogPropertyDAOTest extends AbstractDAOTestBase<AuditLogProperty> {
    @Inject
    public AuditLogPropertyDAO auditLogPropertyDAO;

    @Override
    public AbstractPaginatingDAO getDAO() {
        return auditLogPropertyDAO;
    }

    @Override
    public AuditLogProperty getNew() {
        AuditLogProperty isd = new AuditLogProperty();
        AuditLog auditLog=new AuditLog();
        auditLog.setAuditEntity("New auditEntity");
         isd.setPropertyName("Property Test");
        isd.setPropertyValue("Property value");
        isd.setAuditLog(auditLog);
        return isd;
    }

    @Override
    public void update(AuditLogProperty instance) {

        AuditLog auditLog=new AuditLog();
        auditLog.setAuditEntity("New auditEntity");
        instance.setAuditLog(auditLog);
        instance.setPropertyName("property test");
        instance.setPropertyValue("property value");
    }


}
