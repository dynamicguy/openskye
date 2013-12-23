package org.openskye.domain.dao;

import org.openskye.domain.AttributeDefinition;
import org.openskye.domain.AttributeInstance;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * DAO for the {@link org.openskye.domain.Channel}
 */
public class AttributeDefinitionDAO extends AbstractPaginatingDAO<AttributeDefinition> {

    public boolean isInUse(AttributeDefinition definition)
    {
        EntityManager em = getEntityManagerProvider().get();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttributeInstance> cq = cb.createQuery(AttributeInstance.class);
        Root<AttributeInstance> root = cq.from(AttributeInstance.class);
        Join<AttributeInstance, AttributeDefinition> joinDefinition = root.join("attributeDefinition");

        cq.select(root);
        cq.where(cb.equal(joinDefinition.get("id"), definition.getId()));

        List<AttributeInstance> attributeInstanceList = em.createQuery(cq).getResultList();

        return (attributeInstanceList.size() > 0) ? true : false;
    }
}

