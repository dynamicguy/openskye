package org.openskye.domain.dao;

import com.google.common.base.Optional;
import org.hibernate.Session;
import org.openskye.domain.AuditLog;
import org.openskye.domain.ObjectEvent;
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

    public Optional<List<AuditLog>> findByUser(String userInfo) {
        List<AuditLog> foundActivities;
        CriteriaQuery<AuditLog> cq = createCriteriaQuery();
        Root<AuditLog> root = cq.from(AuditLog.class);
        Join<AuditLog, User> user = root.join("user");

        if (userInfo.indexOf("@") != -1) {
            cq.where(getCriteriaBuilder().equal(user.get("email"), userInfo));
        } else {
            cq.where(getCriteriaBuilder().or(getCriteriaBuilder().equal(user.get("id"), userInfo), getCriteriaBuilder().equal(user.get("name"), userInfo)));
        }

        foundActivities = getEntityManagerProvider().get().createQuery(cq).getResultList();

        if (foundActivities.size() > 0) {
            return Optional.of(foundActivities);
        } else {
            return Optional.absent();
        }
    }

    public Optional<List<AuditLog>> findByObject(String objectId) {
        List<AuditLog> foundActivities;
        CriteriaQuery<AuditLog> cq = createCriteriaQuery();
        Root<AuditLog> root = cq.from(AuditLog.class);

        cq.where(getCriteriaBuilder().equal(root.get("objectAffected"), objectId));
        cq.orderBy(getCriteriaBuilder().asc(root.get("createdAt")));


        foundActivities = getEntityManagerProvider().get().createQuery(cq).getResultList();

        if (foundActivities.size() > 0) {
            return Optional.of(foundActivities);
        } else {
            return Optional.absent();
        }
    }

    /**
     * Check for the existence of objects having a specific object event
     * @param objectId id of ObjectMetatdata
     * @param objectEvent event to check
     * @return
     */
    public boolean isObjectWithEventAudited(String objectId, ObjectEvent objectEvent) {
        Session session = (Session) currentEntityManager().getDelegate();

        return (((Number) session.createQuery("select count(*) from AuditLog al where al.objectAffected = :oa and al.objectEvent = :oe").
                setParameter("oa", objectId).setParameter("oe", objectEvent).uniqueResult()).longValue() > 0);

    }

}
