package org.openskye.domain.dao;

import org.openskye.domain.*;

import javax.persistence.Query;
import javax.persistence.criteria.*;

/**
 * DAO for the {@link org.openskye.domain.Channel}
 */
public class AttributeDefinitionDAO extends AbstractPaginatingDAO<AttributeDefinition> {

    public boolean isInUse(AttributeDefinition definition) {
        Query query = currentEntityManager().createQuery(
                "SELECT count(ai) " +
                        "FROM AttributeInstance ai " +
                        "JOIN ai.attributeDefinition ad " +
                        "where ad = :attributeDefinition");

        return ((Long) query.setParameter("attributeDefinition", definition).getSingleResult() > 0);
    }

    public Iterable<AttributeDefinition> getAll(Channel channel) {
        CriteriaBuilder builder = getEntityManagerProvider().get().getCriteriaBuilder();
        CriteriaQuery<AttributeDefinition> query = builder.createQuery(AttributeDefinition.class);
        Root<Channel> rootChannel = query.from(Channel.class);
        Join<Channel, RetentionPolicy> joinPolicy = rootChannel.join("retentionPolicy");
        Join<RetentionPolicy, MetadataTemplate> joinTemplate = joinPolicy.join("metadataTemplate");
        ListJoin<MetadataTemplate, AttributeDefinition> joinDefinitions = joinTemplate.joinList("attributeDefinitions");

        query.select(joinDefinitions);

        query.where(builder.equal(rootChannel, channel));

        return getEntityManagerProvider().get().createQuery(query).getResultList();

    }

    public Iterable<AttributeDefinition> getUnused(Channel channel) {
        CriteriaBuilder builder = getEntityManagerProvider().get().getCriteriaBuilder();
        CriteriaQuery<AttributeDefinition> query = builder.createQuery(AttributeDefinition.class);
        Root<Channel> rootChannel = query.from(Channel.class);
        Join<Channel, RetentionPolicy> joinPolicy = rootChannel.join("retentionPolicy");
        Join<RetentionPolicy, MetadataTemplate> joinTemplate = joinPolicy.join("metadataTemplate");
        ListJoin<MetadataTemplate, AttributeDefinition> joinDefinitions = joinTemplate.joinList("attributeDefinitions");
        ListJoin<Channel, AttributeInstance> joinInstances = rootChannel.joinList("attributeInstances");

        query.select(joinDefinitions);

        query.where(builder.and(
                builder.equal(rootChannel, channel),
                builder.not(joinInstances.get("attributeDefinition").in(joinDefinitions))
        ));

        return getEntityManagerProvider().get().createQuery(query).getResultList();
    }
}