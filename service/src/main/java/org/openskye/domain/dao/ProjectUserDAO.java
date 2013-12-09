package org.openskye.domain.dao;

import com.google.common.base.Optional;
import org.openskye.domain.ProjectUser;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * User: atcmostafavi
 * Date: 12/6/13
 * Time: 2:30 PM
 * Project: platform
 */
public class ProjectUserDAO extends AbstractPaginatingDAO<ProjectUser> {

    public Optional<List<ProjectUser>> findByUser(String userId){
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<ProjectUser> criteria = builder.createQuery(ProjectUser.class);
        Root<ProjectUser> projectUserRoot = criteria.from(ProjectUser.class);
        criteria.select(projectUserRoot);
        criteria.where(builder.equal(projectUserRoot.get("user"), userId));
        List<ProjectUser> projectUsers = currentEntityManager().createQuery(criteria).getResultList();
        if (projectUsers.size() == 0) {
            return Optional.absent();
        } else {
            return Optional.of(projectUsers);
        }
    }
}
