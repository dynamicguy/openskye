package org.openskye.domain.dao;

import com.google.common.base.Optional;
import org.openskye.domain.Node;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * DAO for the {@link org.openskye.domain.Node}
 */
public class NodeDAO extends AbstractPaginatingDAO<Node> {

    public Optional<Node> findByHostname(String hostname) {

        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<org.openskye.domain.Node> criteria = builder.createQuery(Node.class);
        Root<org.openskye.domain.Node> userRoot = criteria.from(Node.class);
        criteria.select(userRoot);
        criteria.where(builder.equal(userRoot.get("hostname"), hostname));
        List<Node> users = currentEntityManager().createQuery(criteria).getResultList();
        if (users.size() == 0) {
            return com.google.common.base.Optional.absent();
        } else {
            return com.google.common.base.Optional.of(users.get(0));
        }
    }

}
