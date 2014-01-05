package org.openskye.domain.dao;

import org.openskye.domain.AttributeDefinition;

import javax.persistence.Query;

/**
 * DAO for the {@link org.openskye.domain.Channel}
 */
public class AttributeDefinitionDAO extends AbstractPaginatingDAO<AttributeDefinition> {

    public boolean isInUse(AttributeDefinition definition)
    {
        Query query = currentEntityManager().createQuery(
                "SELECT count(*) " +
                "FROM AttributeInstance ai " +
                "JOIN ai.attributeDefinition ad " +
                "where ad = :attributeDefinition");

        return ((Long) query.setParameter("attributeDefinition", definition).getSingleResult() > 0);
    }
}

