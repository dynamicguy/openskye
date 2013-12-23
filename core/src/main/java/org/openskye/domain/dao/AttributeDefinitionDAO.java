package org.openskye.domain.dao;

import org.openskye.domain.AttributeDefinition;
import org.openskye.domain.AttributeInstance;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * DAO for the {@link org.openskye.domain.Channel}
 */
public class AttributeDefinitionDAO extends AbstractPaginatingDAO<AttributeDefinition> {

    public boolean isInUse(AttributeDefinition definition)
    {
        String queryString = "SELECT ai FROM AttributeInstance ai WHERE ai.attributeDefinition.id = '" +
                definition.getId() +
                "'";
        EntityManager em = getEntityManagerProvider().get();
        List<AttributeInstance> attributeInstanceList = em.createQuery(queryString, AttributeInstance.class).getResultList();

        return (attributeInstanceList.size() > 0) ? true : false;
    }
}

