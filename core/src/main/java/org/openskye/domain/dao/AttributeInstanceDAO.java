package org.openskye.domain.dao;

import org.openskye.domain.AttributeInstance;
import org.openskye.domain.Channel;

import javax.persistence.TypedQuery;

/**
 * DAO for the {@link org.openskye.domain.Domain}
 */
public class AttributeInstanceDAO extends AbstractPaginatingDAO<AttributeInstance> {

    public Iterable<AttributeInstance> getInstances(Channel channel)
    {
        TypedQuery<AttributeInstance> query = getEntityManagerProvider().get().createQuery(
                "SELECT ai " +
                "FROM AttributeInstance ai " +
                "WHERE ai.channel = :channel",
                AttributeInstance.class);

        query.setParameter("channel", channel);

        return query.getResultList();
    }
}
