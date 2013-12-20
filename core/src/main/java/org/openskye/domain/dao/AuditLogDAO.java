package org.openskye.domain.dao;

import com.google.common.base.Optional;
import org.openskye.domain.AuditLog;
import org.openskye.domain.User;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * DAO for the {@link org.openskye.domain.AuditLog}
 */
public class AuditLogDAO extends AbstractPaginatingDAO<AuditLog> {

    @Override
    public boolean isAudited() {
        return false;
    }

    public Optional<List<AuditLog>> findByUser(String userInfo){
        List<AuditLog> foundActivities;
        CriteriaQuery<AuditLog> cq = createCriteriaQuery();
        Root<AuditLog> root = cq.from(AuditLog.class);
        Join<AuditLog, User> user = root.join("user");

        if(userInfo.indexOf("@")!=0){
            getCriteriaBuilder().equal(user.get("email"), userInfo);
        }
        else{
            getCriteriaBuilder().equal(user.get("id"), userInfo);
        }

        foundActivities = getEntityManagerProvider().get().createQuery(cq).getResultList();

        if(foundActivities.size()>0){
            return Optional.of(foundActivities);
        }
        else{
            return Optional.absent();
        }
    }

}
