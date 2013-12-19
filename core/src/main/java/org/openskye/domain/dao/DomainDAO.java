package org.openskye.domain.dao;

import com.google.common.base.Optional;
import org.openskye.domain.Domain;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * DAO for the {@link Domain}
 */
public class DomainDAO extends AbstractPaginatingDAO<Domain> {
    public Optional<Domain> findByName(String name) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Domain> criteria = builder.createQuery(Domain.class);
        Root<Domain> domainRoot = criteria.from(Domain.class);
        criteria.select(domainRoot);
        criteria.where(builder.equal(domainRoot.get("name"), name));
        List<Domain> domains = currentEntityManager().createQuery(criteria).getResultList();
        if (domains.size() == 0) {
            return Optional.absent();
        } else {
            return Optional.of(domains.get(0));
        }
    }
}
